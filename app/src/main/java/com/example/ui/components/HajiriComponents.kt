package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Weekend
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.data.model.AttendanceStatus
import com.example.domain.DateUtils
import com.example.ui.theme.AmberDarkInk
import com.example.ui.theme.AmberTint
import com.example.ui.theme.AmberWarm
import com.example.ui.theme.CrimsonAbsent
import com.example.ui.theme.CrimsonTint
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MintCardTint
import com.example.ui.theme.NumericStatStyle
import com.example.ui.theme.SlateOff
import com.example.ui.theme.SlateOffTint
import com.example.ui.theme.TealLeave
import com.example.ui.theme.TealTint

data class StatusStyle(
    val accent: Color,
    val container: Color,
    val onContainer: Color,
    val icon: ImageVector
)

@Composable
fun statusStyleFor(status: AttendanceStatus): StatusStyle {
    return when (status) {
        AttendanceStatus.PRESENT -> StatusStyle(
            accent = EmeraldPrimary,
            container = MintCardTint,
            onContainer = EmeraldDeep,
            icon = Icons.Rounded.CheckCircle
        )
        AttendanceStatus.HALF_DAY -> StatusStyle(
            accent = AmberWarm,
            container = AmberTint,
            onContainer = AmberDarkInk,
            icon = Icons.Rounded.Schedule
        )
        AttendanceStatus.ABSENT -> StatusStyle(
            accent = CrimsonAbsent,
            container = CrimsonTint,
            onContainer = Color(0xFF8C1D1D),
            icon = Icons.Rounded.Cancel
        )
        AttendanceStatus.PAID_LEAVE -> StatusStyle(
            accent = TealLeave,
            container = TealTint,
            onContainer = Color(0xFF084C61),
            icon = Icons.Rounded.BeachAccess
        )
        AttendanceStatus.WEEKLY_OFF -> StatusStyle(
            accent = SlateOff,
            container = SlateOffTint,
            onContainer = Color(0xFF314039),
            icon = Icons.Rounded.Weekend
        )
    }
}

@Composable
fun HeroHeaderCard(
    todayDisplayDate: String,
    monthLabel: String,
    monthlyEarned: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        color = EmeraldDeep
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = stringResource(R.string.cd_hero_banner),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                EmeraldDeep.copy(alpha = 0.94f),
                                EmeraldPrimary.copy(alpha = 0.80f),
                                EmeraldDeep.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontSize = 26.sp,
                                lineHeight = 32.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.app_tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.90f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = monthLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = monthlyEarned,
                                style = NumericStatStyle.copy(fontSize = 18.sp),
                                color = Color(0xFFFDE68A)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.22f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EventAvailable,
                            contentDescription = null,
                            tint = Color(0xFFA7F3C7),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = todayDisplayDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthSwitcherBar(
    monthIso: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCurrentMonth = monthIso == DateUtils.currentMonthIso()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.cd_previous_month),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = DateUtils.formatHindiMonth(monthIso),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isCurrentMonth) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onResetToCurrentMonth)
                            .testTag("reset_current_month_chip")
                    ) {
                        Text(
                            text = "इस माह",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.cd_next_month),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatTileCard(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = value,
                style = NumericStatStyle.copy(fontSize = 22.sp, lineHeight = 26.sp),
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HourStepperField(
    label: String,
    valueText: String,
    onValueChange: (String) -> Unit,
    onStepMinus: () -> Unit,
    onStepPlus: () -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = onStepMinus,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("${testTagPrefix}_minus"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = stringResource(R.string.cd_decrease)
                )
            }

            OutlinedTextField(
                value = valueText,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = NumericStatStyle.copy(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                ),
                suffix = {
                    Text(
                        text = "घंटे",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("${testTagPrefix}_input")
            )

            FilledTonalIconButton(
                onClick = onStepPlus,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("${testTagPrefix}_plus"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.cd_increase)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HajiriDatePickerDialog(
    initialDateIso: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DateUtils.dateIsoToUtcMillis(initialDateIso)
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        onDateSelected(DateUtils.utcMillisToDateIso(millis))
                    }
                    onDismiss()
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("चुनें")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
