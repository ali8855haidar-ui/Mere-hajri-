package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.PaymentEntity
import com.example.data.model.SalarySettings
import com.example.data.repository.HajiriRepository
import com.example.domain.DateUtils
import com.example.domain.MonthlyReport
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HajiriUiState(
    val selectedMonthIso: String = DateUtils.currentMonthIso(),
    val selectedDateIso: String = DateUtils.todayIso(),
    val editorStatus: AttendanceStatus = AttendanceStatus.PRESENT,
    val editorHoursText: String = "8",
    val editorOtText: String = "0",
    val isEditingExistingRecord: Boolean = false,
    val dailyWageText: String = "600",
    val otRateText: String = "100",
    val paymentAmountText: String = "",
    val paymentDateIso: String = DateUtils.todayIso(),
    val paymentNoteText: String = "",
    val settings: SalarySettings = SalarySettings(),
    val attendanceRecords: List<AttendanceEntity> = emptyList(),
    val payments: List<PaymentEntity> = emptyList(),
    val monthlyReport: MonthlyReport = MonthlyReport()
)

sealed interface UiFeedbackEvent {
    data class ShowMessage(val text: String, val isError: Boolean = false) : UiFeedbackEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class HajiriViewModel(
    private val repository: HajiriRepository
) : ViewModel() {

    private val _selectedMonthIso = MutableStateFlow(DateUtils.currentMonthIso())
    private val _formState = MutableStateFlow(FormDraftState())
    private val _feedbackEvents = MutableSharedFlow<UiFeedbackEvent>()
    val feedbackEvents: SharedFlow<UiFeedbackEvent> = _feedbackEvents.asSharedFlow()

    private val attendanceFlow = _selectedMonthIso.flatMapLatest { month ->
        repository.observeAttendanceByMonth(month)
    }

    private val paymentsFlow = _selectedMonthIso.flatMapLatest { month ->
        repository.observePaymentsByMonth(month)
    }

    val uiState: StateFlow<HajiriUiState> = combine(
        _selectedMonthIso,
        _formState,
        repository.salarySettings,
        attendanceFlow,
        paymentsFlow
    ) { monthIso, form, settings, attendance, payments ->
        val report = MonthlyReport.calculate(attendance, payments, settings)
        HajiriUiState(
            selectedMonthIso = monthIso,
            selectedDateIso = form.selectedDateIso,
            editorStatus = form.editorStatus,
            editorHoursText = form.editorHoursText,
            editorOtText = form.editorOtText,
            isEditingExistingRecord = form.isEditingExistingRecord,
            dailyWageText = form.dailyWageText ?: formatNumberForInput(settings.dailyWage),
            otRateText = form.otRateText ?: formatNumberForInput(settings.otRate),
            paymentAmountText = form.paymentAmountText,
            paymentDateIso = form.paymentDateIso,
            paymentNoteText = form.paymentNoteText,
            settings = settings,
            attendanceRecords = attendance,
            payments = payments,
            monthlyReport = report
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HajiriUiState()
    )

    init {
        loadAttendanceForDate(DateUtils.todayIso())
    }

    fun changeMonth(deltaMonths: Int) {
        _selectedMonthIso.update { DateUtils.shiftMonth(it, deltaMonths) }
    }

    fun selectMonth(monthIso: String) {
        _selectedMonthIso.value = monthIso
    }

    fun selectAttendanceDate(dateIso: String) {
        _selectedMonthIso.value = DateUtils.monthOfDate(dateIso)
        loadAttendanceForDate(dateIso)
    }

    fun editAttendanceRecord(record: AttendanceEntity) {
        _selectedMonthIso.value = DateUtils.monthOfDate(record.dateIso)
        _formState.update {
            it.copy(
                selectedDateIso = record.dateIso,
                editorStatus = record.statusEnum(),
                editorHoursText = formatNumberForInput(record.dutyHours),
                editorOtText = formatNumberForInput(record.otHours),
                isEditingExistingRecord = true
            )
        }
    }

    private fun loadAttendanceForDate(dateIso: String) {
        viewModelScope.launch {
            val existing = repository.getAttendanceForDate(dateIso)
            _formState.update { form ->
                if (existing != null) {
                    form.copy(
                        selectedDateIso = dateIso,
                        editorStatus = existing.statusEnum(),
                        editorHoursText = formatNumberForInput(existing.dutyHours),
                        editorOtText = formatNumberForInput(existing.otHours),
                        isEditingExistingRecord = true
                    )
                } else {
                    form.copy(
                        selectedDateIso = dateIso,
                        editorStatus = AttendanceStatus.PRESENT,
                        editorHoursText = "8",
                        editorOtText = "0",
                        isEditingExistingRecord = false
                    )
                }
            }
        }
    }

    fun selectEditorStatus(status: AttendanceStatus) {
        _formState.update { form ->
            val currentHours = form.editorHoursText.toDoubleOrNull()
            val nextHoursText = when {
                status == AttendanceStatus.ABSENT || status == AttendanceStatus.WEEKLY_OFF -> "0"
                status == AttendanceStatus.HALF_DAY && (currentHours == null || currentHours == 8.0 || currentHours == 0.0) -> "4"
                (status == AttendanceStatus.PRESENT || status == AttendanceStatus.PAID_LEAVE) && (currentHours == null || currentHours == 0.0 || currentHours == 4.0) -> "8"
                else -> form.editorHoursText
            }
            val nextOtText = if (status == AttendanceStatus.ABSENT || status == AttendanceStatus.WEEKLY_OFF) {
                "0"
            } else {
                form.editorOtText
            }
            form.copy(
                editorStatus = status,
                editorHoursText = nextHoursText,
                editorOtText = nextOtText
            )
        }
    }

    fun updateDutyHoursText(text: String) {
        if (isValidDecimalInput(text)) {
            _formState.update { it.copy(editorHoursText = text) }
        }
    }

    fun stepDutyHours(delta: Double) {
        val current = _formState.value.editorHoursText.toDoubleOrNull() ?: 0.0
        val next = (current + delta).coerceIn(0.0, 24.0)
        _formState.update { it.copy(editorHoursText = formatNumberForInput(next)) }
    }

    fun updateOtHoursText(text: String) {
        if (isValidDecimalInput(text)) {
            _formState.update { it.copy(editorOtText = text) }
        }
    }

    fun stepOtHours(delta: Double) {
        val current = _formState.value.editorOtText.toDoubleOrNull() ?: 0.0
        val next = (current + delta).coerceIn(0.0, 24.0)
        _formState.update { it.copy(editorOtText = formatNumberForInput(next)) }
    }

    fun saveAttendance(savedMessage: String) {
        val form = _formState.value
        val hours = (form.editorHoursText.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
        val ot = (form.editorOtText.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)

        viewModelScope.launch {
            repository.markAttendance(
                dateIso = form.selectedDateIso,
                status = form.editorStatus,
                dutyHours = hours,
                otHours = ot
            )
            _selectedMonthIso.value = DateUtils.monthOfDate(form.selectedDateIso)
            _formState.update { it.copy(isEditingExistingRecord = true) }
            _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(savedMessage))
        }
    }

    fun deleteAttendance(dateIso: String, deletedMessage: String) {
        viewModelScope.launch {
            repository.deleteAttendance(dateIso)
            if (_formState.value.selectedDateIso == dateIso) {
                loadAttendanceForDate(dateIso)
            }
            _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(deletedMessage))
        }
    }

    fun updateDailyWageText(text: String) {
        if (isValidDecimalInput(text)) {
            _formState.update { it.copy(dailyWageText = text) }
        }
    }

    fun updateOtRateText(text: String) {
        if (isValidDecimalInput(text)) {
            _formState.update { it.copy(otRateText = text) }
        }
    }

    fun saveSettings(savedMessage: String, errorMessage: String) {
        val currentState = uiState.value
        val wage = currentState.dailyWageText.toDoubleOrNull()
        val otRate = currentState.otRateText.toDoubleOrNull()
        if (wage == null || wage < 0.0 || otRate == null || otRate < 0.0) {
            viewModelScope.launch {
                _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(errorMessage, isError = true))
            }
            return
        }
        viewModelScope.launch {
            repository.saveSettings(dailyWage = wage, otRate = otRate)
            _formState.update {
                it.copy(
                    dailyWageText = formatNumberForInput(wage),
                    otRateText = formatNumberForInput(otRate)
                )
            }
            _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(savedMessage))
        }
    }

    fun updatePaymentAmountText(text: String) {
        if (isValidDecimalInput(text)) {
            _formState.update { it.copy(paymentAmountText = text) }
        }
    }

    fun updatePaymentDateIso(dateIso: String) {
        _formState.update { it.copy(paymentDateIso = dateIso) }
    }

    fun updatePaymentNoteText(text: String) {
        _formState.update { it.copy(paymentNoteText = text) }
    }

    fun addPayment(addedMessage: String, invalidAmountMessage: String) {
        val form = _formState.value
        val amount = form.paymentAmountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            viewModelScope.launch {
                _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(invalidAmountMessage, isError = true))
            }
            return
        }
        viewModelScope.launch {
            repository.addPayment(
                amount = amount,
                dateIso = form.paymentDateIso,
                note = form.paymentNoteText
            )
            _selectedMonthIso.value = DateUtils.monthOfDate(form.paymentDateIso)
            _formState.update {
                it.copy(
                    paymentAmountText = "",
                    paymentNoteText = ""
                )
            }
            _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(addedMessage))
        }
    }

    fun deletePayment(id: Long, deletedMessage: String) {
        viewModelScope.launch {
            repository.deletePayment(id)
            _feedbackEvents.emit(UiFeedbackEvent.ShowMessage(deletedMessage))
        }
    }

    private fun isValidDecimalInput(input: String): Boolean {
        if (input.isEmpty()) return true
        return input.matches(Regex("^\\d*\\.?\\d{0,2}$"))
    }

    private fun formatNumberForInput(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            DateUtils.formatHours(value)
        }
    }

    private data class FormDraftState(
        val selectedDateIso: String = DateUtils.todayIso(),
        val editorStatus: AttendanceStatus = AttendanceStatus.PRESENT,
        val editorHoursText: String = "8",
        val editorOtText: String = "0",
        val isEditingExistingRecord: Boolean = false,
        val dailyWageText: String? = null,
        val otRateText: String? = null,
        val paymentAmountText: String = "",
        val paymentDateIso: String = DateUtils.todayIso(),
        val paymentNoteText: String = ""
    )

    class Factory(private val repository: HajiriRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HajiriViewModel(repository) as T
        }
    }
}
