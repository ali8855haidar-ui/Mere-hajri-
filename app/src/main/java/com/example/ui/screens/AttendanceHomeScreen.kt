package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AttendanceStatus
import com.example.domain.DateUtils
import com.example.ui.HajiriUiState
import com.example.ui.components.HajiriDatePickerDialog
import com.example.ui.components.HeroHeaderCard
import com.example.ui.components.HourStepperField
import com.example.ui.components.MonthSwitcherBar
import com.example.ui.components.StatTileCard
import com.example.ui.components.statusStyleFor
import com.example.ui.theme.AmberTint
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CrimsonAbsent
import com.example.ui.theme.CrimsonTint
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintCardTint
import com.example.ui.theme.NumericStatStyle
import com.example.ui.theme.TealLeave
import com.example.ui.theme.TealTint

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttendanceHomeScreen(
    uiState: HajiriUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    onSelectDate: (String) -> Unit,
    onSelectStatus: (AttendanceStatus) -> Unit,
    onDutyHoursChange: (String) -> Unit,
    onStepDutyHours: (Double) -> Unit,
    onOtHoursChange: (String) -> Unit,
    onStepOtHours: (Double) -> Unit,
    onSaveAttendance: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val report = uiState.monthlyReport
    val todayIso = remember { DateUtils.todayIso() }

    val previewDuty = uiState.editorHoursText.toDoubleOrNull() ?: 0.0
    val previewOt = uiState.editorOtText.toDoubleOrNull() ?: 0.0
    val previewEarning = (uiState.editorStatus.dayWeight * uiState.settings.dailyWage) +
        (previewOt.coerceAtLeast(0.0) * uiState.settings.otRate)

    if (showDatePicker) {
        HajiriDatePickerDialog(
            initialDateIso = uiState.selectedDateIso,
            onDateSelected = onSelectDate,
            onDismiss = { showDatePicker = false }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeroHeaderCard(
                todayDisplayDate = DateUtils.formatHindiFullDate(todayIso),
                monthLabel = DateUtils.formatHindiMonth(uiState.selectedMonthIso),
                monthlyEarned = DateUtils.formatRupees(report.totalEarned),
                modifier = Modifier.widthIn(max = 680.dp)
            )
        }

        item {
            MonthSwitcherBar(
                monthIso = uiState.selectedMonthIso,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onResetToCurrentMonth = onResetToCurrentMonth,
                modifier = Modifier.widthIn(max = 680.dp)
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTileCard(
                        label = stringResource(R.string.stat_total_present),
                        value = "${report.presentCount} दिन",
                        icon = Icons.Rounded.CheckCircle,
                        accentColor = EmeraldPrimary,
                        containerColor = MintCardTint,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_present")
                    )
                    StatTileCard(
                        label = stringResource(R.string.stat_total_ot),
                        value = "${DateUtils.formatHours(report.totalOtHours)} घं",
                        icon = Icons.Rounded.MoreTime,
                        accentColor = AmberWarm,
                        containerColor = AmberTint,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_ot")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatTileCard(
                        label = stringResource(R.string.stat_total_salary),
                        value = DateUtils.formatRupees(report.totalEarned),
                        icon = Icons.Rounded.CurrencyRupee,
                        accentColor = TealLeave,
                        containerColor = TealTint,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_salary")
                    )
                    val dueColor = if (report.balanceDue > 0) CrimsonAbsent else EmeraldPrimary
                    val dueBg = if (report.balanceDue > 0) CrimsonTint else MintCardTint
                    StatTileCard(
                        label = stringResource(R.string.stat_payment_due),
                        value = DateUtils.formatRupees(report.balanceDue),
                        icon = Icons.Rounded.AccountBalanceWallet,
                        accentColor = dueColor,
                        containerColor = dueBg,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_due")
                    )
                }
            }
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.EditCalendar,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.mark_attendance_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (uiState.selectedDateIso != todayIso) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .clickable { onSelectDate(todayIso) }
                                    .testTag("today_date_chip")
                            ) {
                                Text(
                                    text = "आज चुनें",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showDatePicker = true }
                            .testTag("attendance_date_picker_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarToday,
                                    contentDescription = stringResource(R.string.cd_pick_date),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = DateUtils.formatHindiFullDate(uiState.selectedDateIso),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = uiState.selectedDateIso,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "बदलें",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    AnimatedVisibility(visible = uiState.isEditingExistingRecord) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "इस तारीख की हाजिरी पहले से दर्ज है — आप इसे अपडेट कर सकते हैं",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Text(
                        text = "हाजिरी की स्थिति चुनें",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AttendanceStatus.entries.forEach { status ->
                            val selected = uiState.editorStatus == status
                            val style = statusStyleFor(status)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) style.accent else style.container.copy(alpha = 0.7f),
                                border = BorderStroke(
                                    width = if (selected) 0.dp else 1.dp,
                                    color = if (selected) Color.Transparent else style.accent.copy(alpha = 0.35f)
                                ),
                                shadowElevation = if (selected) 2.dp else 0.dp,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onSelectStatus(status) }
                                    .testTag("status_chip_${status.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = style.icon,
                                        contentDescription = null,
                                        tint = if (selected) Color.White else style.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = status.labelHindi,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (selected) Color.White else style.onContainer
                                    )
                                }
                            }
                        }
                    }

                    HourStepperField(
                        label = stringResource(R.string.duty_hours_label),
                        valueText = uiState.editorHoursText,
                        onValueChange = onDutyHoursChange,
                        onStepMinus = { onStepDutyHours(-0.5) },
                        onStepPlus = { onStepDutyHours(0.5) },
                        testTagPrefix = "duty_hours"
                    )

                    HourStepperField(
                        label = stringResource(R.string.ot_hours_label),
                        valueText = uiState.editorOtText,
                        onValueChange = onOtHoursChange,
                        onStepMinus = { onStepOtHours(-0.5) },
                        onStepPlus = { onStepOtHours(0.5) },
                        testTagPrefix = "ot_hours"
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "इस दिन की अनुमानित कमाई",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "ड्यूटी ${DateUtils.formatHours(previewDuty.coerceAtLeast(0.0))} घं • OT ${DateUtils.formatHours(previewOt.coerceAtLeast(0.0))} घं",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = DateUtils.formatRupees(previewEarning),
                                style = NumericStatStyle.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Button(
                        onClick = onSaveAttendance,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("save_attendance_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isEditingExistingRecord) {
                                "हाजिरी अपडेट करें"
                            } else {
                                stringResource(R.string.save_attendance_btn)
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
