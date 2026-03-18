package com.kpn.falcon.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors

/**
 * Labelled switch toggle.
 * Design spec: green when ON (AccentGreen thumb track).
 */
@Composable
fun KPNToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = KPNColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { if (enabled) onCheckedChange(it) },
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = KPNColors.Surface,
                checkedTrackColor = KPNColors.AccentGreen,
                uncheckedThumbColor = KPNColors.Surface,
                uncheckedTrackColor = KPNColors.Border
            )
        )
    }
}
