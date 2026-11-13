package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AttendanceStatus
import com.example.domain.DateUtils
import com.example.domain.MonthlyReport
import com.example.ui.HajiriUiState
import com.example.ui.components.MonthSwitcherBar
import com.example.ui.components.statusStyleFor
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CrimsonAbsent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.NumericStatStyle
import com.example.ui.theme.SlateOff
import com.example.ui.theme.TealLeave

@Composable
fun ReportSettingsScreen(
    uiState: HajiriUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    onDailyWageChange: (String) -> Unit,
    onOtRateChange: (String) -> Unit,
    onSaveSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val report = uiState.monthlyReport
    val previewWage = uiState.dailyWageText.toDoubleOrNull() ?: uiState.settings.dailyWage
    val previewOtRate = uiState.otRateText.toDoubleOrNull() ?: uiState.settings.otRate
    val sampleDayTotal = previewWage + (2.0 * previewOtRate)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                                imageVector = Icons.Rounded.Assessment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.monthly_report_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${report.totalMarkedDays} दिन दर्ज",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AttendanceDistributionBar(report = report)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportCountRow(
                            status = AttendanceStatus.PRESENT,
                            count = "${report.presentCount} दिन"
                        )
                        ReportCountRow(
                            status = AttendanceStatus.HALF_DAY,
                            count = "${report.halfDayCount} दिन"
                        )
                        ReportCountRow(
                            status = AttendanceStatus.PAID_LEAVE,
                            count = "${report.paidLeaveCount} दिन"
                        )
                        ReportCountRow(
                            status = AttendanceStatus.ABSENT,
                            count = "${report.absentCount} दिन"
                        )
                        ReportCountRow(
                            status = AttendanceStatus.WEEKLY_OFF,
                            count = "${report.weeklyOffCount} दिन"
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportSummaryTile(
                            label = "कुल ड्यूटी",
                            value = "${DateUtils.formatHours(report.totalWorkHours)} घंटे",
                            modifier = Modifier.weight(1f)
                        )
                        ReportSummaryTile(
                            label = "कुल OT",
                            value = "${DateUtils.formatHours(report.totalOtHours)} घंटे",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportSummaryTile(
                            label = "कुल कमाई",
                            value = DateUtils.formatRupees(report.totalEarned),
                            valueColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        ReportSummaryTile(
                            label = "पेमेंट बाकी",
                            value = DateUtils.formatRupees(report.balanceDue),
                            valueColor = if (report.balanceDue > 0) CrimsonAbsent else EmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.salary_settings_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = uiState.dailyWageText,
                        onValueChange = onDailyWageChange,
                        label = { Text(stringResource(R.string.daily_wage_label)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.CurrencyRupee,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("daily_wage_input")
                    )

                    OutlinedTextField(
                        value = uiState.otRateText,
                        onValueChange = onOtRateChange,
                        label = { Text(stringResource(R.string.ot_rate_label)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.MoreTime,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ot_rate_input")
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "उदाहरण: 1 दिन Present + 2 घंटे OT = ${DateUtils.formatRupees(sampleDayTotal)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    Button(
                        onClick = onSaveSettings,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("save_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.save_settings_btn),
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

@Composable
private fun AttendanceDistributionBar(report: MonthlyReport) {
    val total = report.totalMarkedDays
    val emptyBarColor = MaterialTheme.colorScheme.surfaceVariant
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(50))
    ) {
        if (total <= 0) {
            drawRoundRect(
                color = emptyBarColor,
                size = size,
                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
            )
            return@Canvas
        }
        val segments = listOf(
            report.presentCount to EmeraldPrimary,
            report.halfDayCount to AmberWarm,
            report.paidLeaveCount to TealLeave,
            report.absentCount to CrimsonAbsent,
            report.weeklyOffCount to SlateOff
        ).filter { it.first > 0 }

        var startX = 0f
        segments.forEach { (count, color) ->
            val segmentWidth = (count.toFloat() / total.toFloat()) * size.width
            drawRect(
                color = color,
                topLeft = Offset(startX, 0f),
                size = Size(segmentWidth, size.height)
            )
            startX += segmentWidth
        }
    }
}

@Composable
private fun ReportCountRow(
    status: AttendanceStatus,
    count: String
) {
    val style = statusStyleFor(status)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(style.accent)
            )
            Text(
                text = status.labelHindi,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Surface(
            shape = RoundedCornerShape(50),
            color = style.container
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.labelLarge,
                color = style.onContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ReportSummaryTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = NumericStatStyle.copy(fontSize = 18.sp),
                color = valueColor
            )
        }
    }
}
