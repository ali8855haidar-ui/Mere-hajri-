package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.screens.AttendanceHomeScreen
import com.example.ui.screens.LedgerScreen
import com.example.ui.screens.ReportSettingsScreen

enum class HajiriTab(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    ATTENDANCE(
        titleRes = R.string.nav_attendance,
        selectedIcon = Icons.Rounded.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        testTag = "nav_tab_attendance"
    ),
    LEDGER(
        titleRes = R.string.nav_ledger,
        selectedIcon = Icons.Rounded.Payments,
        unselectedIcon = Icons.Outlined.Payments,
        testTag = "nav_tab_ledger"
    ),
    REPORT(
        titleRes = R.string.nav_report,
        selectedIcon = Icons.Rounded.Assessment,
        unselectedIcon = Icons.Outlined.Assessment,
        testTag = "nav_tab_report"
    )
}

@Composable
fun MeriHajiriApp(
    viewModel: HajiriViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var currentTab by rememberSaveable { mutableStateOf(HajiriTab.ATTENDANCE) }

    BackHandler(enabled = currentTab != HajiriTab.ATTENDANCE) {
        currentTab = HajiriTab.ATTENDANCE
    }

    LaunchedEffect(Unit) {
        viewModel.feedbackEvents.collect { event ->
            when (event) {
                is UiFeedbackEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.text)
                }
            }
        }
    }

    val attendanceSavedMsg = stringResource(R.string.attendance_saved_msg)
    val attendanceDeletedMsg = stringResource(R.string.attendance_deleted_msg)
    val settingsSavedMsg = stringResource(R.string.settings_saved_msg)
    val invalidSettingsMsg = stringResource(R.string.invalid_settings_msg)
    val paymentAddedMsg = stringResource(R.string.payment_added_msg)
    val invalidAmountMsg = stringResource(R.string.invalid_amount_msg)
    val paymentDeletedMsg = stringResource(R.string.payment_deleted_msg)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                }
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        HajiriTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            val label = context.getString(tab.titleRes)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        HajiriTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            val label = context.getString(tab.titleRes)
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        HajiriTab.ATTENDANCE -> {
                            AttendanceHomeScreen(
                                uiState = uiState,
                                onPreviousMonth = { viewModel.changeMonth(-1) },
                                onNextMonth = { viewModel.changeMonth(1) },
                                onResetToCurrentMonth = {
                                    viewModel.selectMonth(com.example.domain.DateUtils.currentMonthIso())
                                },
                                onSelectDate = viewModel::selectAttendanceDate,
                                onSelectStatus = viewModel::selectEditorStatus,
                                onDutyHoursChange = viewModel::updateDutyHoursText,
                                onStepDutyHours = viewModel::stepDutyHours,
                                onOtHoursChange = viewModel::updateOtHoursText,
                                onStepOtHours = viewModel::stepOtHours,
                                onSaveAttendance = {
                                    viewModel.saveAttendance(attendanceSavedMsg)
                                }
                            )
                        }

                        HajiriTab.LEDGER -> {
                            LedgerScreen(
                                uiState = uiState,
                                onPreviousMonth = { viewModel.changeMonth(-1) },
                                onNextMonth = { viewModel.changeMonth(1) },
                                onResetToCurrentMonth = {
                                    viewModel.selectMonth(com.example.domain.DateUtils.currentMonthIso())
                                },
                                onPaymentAmountChange = viewModel::updatePaymentAmountText,
                                onPaymentDateChange = viewModel::updatePaymentDateIso,
                                onPaymentNoteChange = viewModel::updatePaymentNoteText,
                                onAddPayment = {
                                    viewModel.addPayment(paymentAddedMsg, invalidAmountMsg)
                                },
                                onDeletePayment = { id ->
                                    viewModel.deletePayment(id, paymentDeletedMsg)
                                },
                                onEditAttendance = { record ->
                                    viewModel.editAttendanceRecord(record)
                                    currentTab = HajiriTab.ATTENDANCE
                                },
                                onDeleteAttendance = { dateIso ->
                                    viewModel.deleteAttendance(dateIso, attendanceDeletedMsg)
                                }
                            )
                        }

                        HajiriTab.REPORT -> {
                            ReportSettingsScreen(
                                uiState = uiState,
                                onPreviousMonth = { viewModel.changeMonth(-1) },
                                onNextMonth = { viewModel.changeMonth(1) },
                                onResetToCurrentMonth = {
                                    viewModel.selectMonth(com.example.domain.DateUtils.currentMonthIso())
                                },
                                onDailyWageChange = viewModel::updateDailyWageText,
                                onOtRateChange = viewModel::updateOtRateText,
                                onSaveSettings = {
                                    viewModel.saveSettings(settingsSavedMsg, invalidSettingsMsg)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
