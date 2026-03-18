package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius

/**
 * Single-select chip group (radio behaviour).
 * Design spec: gray background unselected, yellow filled selected, 8dp corner.
 */
@Composable
fun <T> KPNRadioChipGroup(
    options: List<T>,
    selected: T?,
    onSelect: (T) -> Unit,
    labelFor: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FlowChipRow(modifier = modifier) {
        options.forEach { option ->
            val isSelected = option == selected
            SelectableChip(
                label = labelFor(option),
                isSelected = isSelected,
                onClick = { if (enabled) onSelect(option) },
                enabled = enabled
            )
        }
    }
}

/**
 * Multi-select chip group (checkbox behaviour).
 */
@Composable
fun <T> KPNMultiChipGroup(
    options: List<T>,
    selected: List<T>,
    onToggle: (T) -> Unit,
    labelFor: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FlowChipRow(modifier = modifier) {
        options.forEach { option ->
            val isSelected = option in selected
            SelectableChip(
                label = labelFor(option),
                isSelected = isSelected,
                onClick = { if (enabled) onToggle(option) },
                enabled = enabled
            )
        }
    }
}

/**
 * Filter chip row for Properties screen (scrollable horizontal).
 */
@Composable
fun KPNFilterChipRow(
    options: List<String>,
    activeOption: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(options) { option ->
            val isSelected = option == activeOption || (activeOption == null && option == "All")
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (option == "All") null else option) },
                label = { Text(option, style = MaterialTheme.typography.labelMedium) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = KPNColors.Primary,
                    selectedLabelColor = KPNColors.PrimaryText,
                    containerColor = KPNColors.Surface,
                    labelColor = KPNColors.TextPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    selectedBorderColor = KPNColors.Primary,
                    borderColor = KPNColors.Border
                )
            )
        }
    }
}

@Composable
private fun SelectableChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .clickable(enabled = enabled, onClick = onClick)
            .background(
                color = if (isSelected) KPNColors.Primary else KPNColors.Background,
                shape = KPNRadius.chip
            )
            .border(
                width = 1.dp,
                color = if (isSelected) KPNColors.Primary else KPNColors.Border,
                shape = KPNRadius.chip
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) KPNColors.PrimaryText else KPNColors.TextPrimary
        )
    }
}

/**
 * Simple wrapping row for chips — avoids external FlowLayout dependency.
 * Items wrap onto a new row when they overflow horizontally.
 */
@Composable
private fun FlowChipRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Int = 8,
    verticalSpacing: Int = 8,
    content: @Composable () -> Unit
) {
    // Using Column + Row approach with wrapping via Layout for CMP compatibility
    // A proper FlowRow is available in Compose 1.5+ material3 experimental
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing.dp)
    ) {
        content()
    }
}
