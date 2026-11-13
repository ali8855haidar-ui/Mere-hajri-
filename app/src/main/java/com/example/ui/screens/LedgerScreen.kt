package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CurrencyRupee
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AttendanceEntity
import com.example.data.model.PaymentEntity
import com.example.domain.DateUtils
import com.example.domain.MonthlyReport
import com.example.ui.HajiriUiState
import com.example.ui.components.HajiriDatePickerDialog
import com.example.ui.components.MonthSwitcherBar
import com.example.ui.components.statusStyleFor
import com.example.ui.theme.CrimsonAbsent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.NumericStatStyle

@Composable
fun LedgerScreen(
    uiState: HajiriUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    onPaymentAmountChange: (String) -> Unit,
    onPaymentDateChange: (String) -> Unit,
    onPaymentNoteChange: (String) -> Unit,
    onAddPayment: () -> Unit,
    onDeletePayment: (Long) -> Unit,
    onEditAttendance: (AttendanceEntity) -> Unit,
    onDeleteAttendance: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPaymentDatePicker by remember { mutableStateOf(false) }

    if (showPaymentDatePicker) {
        HajiriDatePickerDialog(
            initialDateIso = uiState.paymentDateIso,
            onDateSelected = onPaymentDateChange,
            onDismiss = { showPaymentDatePicker = false }
        )
    }

    val report = uiState.monthlyReport

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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.payment_ledger_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LedgerMiniMetric(
                            title = "कुल कमाई",
                            amount = DateUtils.formatRupees(report.totalEarned),
                            accent = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        LedgerMiniMetric(
                            title = "मिला पेमेंट",
                            amount = DateUtils.formatRupees(report.totalPaid),
                            accent = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                        LedgerMiniMetric(
                            title = "बाकी हिसाब",
                            amount = DateUtils.formatRupees(report.balanceDue),
                            accent = if (report.balanceDue > 0) CrimsonAbsent else EmeraldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = uiState.paymentAmountText,
                        onValueChange = onPaymentAmountChange,
                        label = { Text(stringResource(R.string.payment_received_label)) },
                        placeholder = { Text("उदाहरण: 1500") },
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
                            .testTag("payment_amount_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showPaymentDatePicker = true }
                                .testTag("payment_date_picker_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarToday,
                                    contentDescription = stringResource(R.string.cd_pick_date),
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = uiState.paymentDateIso,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        OutlinedTextField(
                            value = uiState.paymentNoteText,
                            onValueChange = onPaymentNoteChange,
                            label = { Text("विवरण") },
                            placeholder = { Text("नकद / एडवांस") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("payment_note_input")
                        )
                    }

                    Button(
                        onClick = onAddPayment,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("add_payment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCard,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.add_payment_btn),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (uiState.payments.isNotEmpty()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Text(
                            text = "मिले हुए पेमेंट की सूची",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        uiState.payments.forEach { payment ->
                            PaymentRow(
                                payment = payment,
                                onDelete = { onDeletePayment(payment.id) }
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.empty_payment_title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.attendance_history_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "${uiState.attendanceRecords.size} दिन",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (uiState.attendanceRecords.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_empty_ledger),
                            contentDescription = stringResource(R.string.cd_empty_ledger),
                            modifier = Modifier
                                .size(160.dp, 120.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = stringResource(R.string.empty_history_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.empty_history_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(
                items = uiState.attendanceRecords,
                key = { it.dateIso }
            ) { record ->
                AttendanceHistoryCard(
                    record = record,
                    dayEarning = MonthlyReport.calculateDayEarning(record, uiState.settings),
                    onEdit = { onEditAttendance(record) },
                    onDelete = { onDeleteAttendance(record.dateIso) },
                    modifier = Modifier.widthIn(max = 680.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun LedgerMiniMetric(
    title: String,
    amount: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = amount,
                style = NumericStatStyle.copy(fontSize = 15.sp),
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PaymentRow(
    payment: PaymentEntity,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = DateUtils.formatRupees(payment.amount),
                    style = NumericStatStyle.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                val subtitle = buildString {
                    append(DateUtils.formatHindiShortDate(payment.dateIso))
                    if (payment.note.isNotBlank()) {
                        append(" • ")
                        append(payment.note)
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("delete_payment_${payment.id}")
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = stringResource(R.string.cd_delete_record),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AttendanceHistoryCard(
    record: AttendanceEntity,
    dayEarning: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = record.statusEnum()
    val style = statusStyleFor(status)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onEdit)
            .testTag("attendance_record_${record.dateIso}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(style.container),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.accent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = DateUtils.formatHindiShortDate(record.dateIso),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = style.container
                        ) {
                            Text(
                                text = status.shortLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = style.onContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "ड्यूटी: ${DateUtils.formatHours(record.dutyHours)} घंटे • OT: ${DateUtils.formatHours(record.otHours)} घंटे • ${DateUtils.formatRupees(dayEarning)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("edit_attendance_${record.dateIso}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(R.string.cd_edit_record),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("delete_attendance_${record.dateIso}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = stringResource(R.string.cd_delete_record),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
