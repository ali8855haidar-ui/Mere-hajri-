package com.example.data.repository

import com.example.data.local.AttendanceDao
import com.example.data.local.PaymentDao
import com.example.data.local.SettingsDataStore
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.PaymentEntity
import com.example.data.model.SalarySettings
import kotlinx.coroutines.flow.Flow

class HajiriRepository(
    private val attendanceDao: AttendanceDao,
    private val paymentDao: PaymentDao,
    private val settingsDataStore: SettingsDataStore
) {
    val salarySettings: Flow<SalarySettings> = settingsDataStore.settingsFlow

    fun observeAttendanceByMonth(monthIso: String): Flow<List<AttendanceEntity>> =
        attendanceDao.observeByMonth(monthIso)

    fun observePaymentsByMonth(monthIso: String): Flow<List<PaymentEntity>> =
        paymentDao.observeByMonth(monthIso)

    suspend fun getAttendanceForDate(dateIso: String): AttendanceEntity? =
        attendanceDao.getByDate(dateIso)

    suspend fun markAttendance(
        dateIso: String,
        status: AttendanceStatus,
        dutyHours: Double,
        otHours: Double
    ) {
        attendanceDao.upsert(
            AttendanceEntity(
                dateIso = dateIso,
                status = status.storageKey,
                dutyHours = dutyHours.coerceAtLeast(0.0),
                otHours = otHours.coerceAtLeast(0.0),
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteAttendance(dateIso: String) {
        attendanceDao.deleteByDate(dateIso)
    }

    suspend fun addPayment(amount: Double, dateIso: String, note: String) {
        paymentDao.insert(
            PaymentEntity(
                amount = amount,
                dateIso = dateIso,
                note = note.trim()
            )
        )
    }

    suspend fun deletePayment(id: Long) {
        paymentDao.deleteById(id)
    }

    suspend fun saveSettings(dailyWage: Double, otRate: Double) {
        settingsDataStore.saveSettings(
            dailyWage = dailyWage.coerceAtLeast(0.0),
            otRate = otRate.coerceAtLeast(0.0)
        )
    }
}
