package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius

/**
 * Date picker field.
 * Taps open a Material3 DatePickerDialog.
 * Returns selected date as epoch milliseconds.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KPNDatePickerField(
    label: String,
    selectedDateMs: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isDeviation: Boolean = false,
    enabled: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label + if (isRequired) " *" else "",
            style = MaterialTheme.typography.bodyMedium,
            color = KPNColors.TextSecondary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isDeviation) KPNColors.DeviationHighlight else KPNColors.Background,
                    shape = KPNRadius.input
                )
                .border(
                    width = 1.5.dp,
                    color = if (isDeviation) KPNColors.AccentOrange else KPNColors.Border,
                    shape = KPNRadius.input
                )
                .clickable(enabled = enabled) { showDialog = true }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDateMs?.let { epochMsToDateString(it) } ?: "Select date",
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedDateMs != null) KPNColors.TextPrimary else KPNColors.TextSecondary.copy(alpha = 0.6f)
            )
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Pick date",
                tint = KPNColors.TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }

        if (isDeviation) DeviationFlag()
    }

    if (showDialog) {
        val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateMs)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onDateSelected(it) }
                    showDialog = false
                }) {
                    Text("OK", color = KPNColors.AccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = KPNColors.AccentOrange)
                }
            }
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = KPNColors.Primary,
                    selectedDayContentColor = KPNColors.PrimaryText,
                    todayDateBorderColor = KPNColors.AccentGreen
                )
            )
        }
    }
}

/**
 * Minimal epoch-to-date string for KMP (no java.time needed).
 * Format: DD MMM YYYY
 */
fun epochMsToDateString(epochMs: Long): String {
    val totalDays = epochMs / (24L * 60 * 60 * 1000)
    // Using Zeller-like algorithm relative to 1 Jan 1970 (Thursday)
    var y = 1970
    var remainingDays = totalDays.toInt()

    while (true) {
        val daysInYear = if (isLeapYear(y)) 366 else 365
        if (remainingDays < daysInYear) break
        remainingDays -= daysInYear
        y++
    }

    val monthLengths = intArrayOf(31, if (isLeapYear(y)) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    var m = 0
    while (m < 12 && remainingDays >= monthLengths[m]) {
        remainingDays -= monthLengths[m]
        m++
    }
    val d = remainingDays + 1
    val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "$d ${monthNames[m]} $y"
}

private fun isLeapYear(y: Int) = (y % 4 == 0 && y % 100 != 0) || y % 400 == 0
