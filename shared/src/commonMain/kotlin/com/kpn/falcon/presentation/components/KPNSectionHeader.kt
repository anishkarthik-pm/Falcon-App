package com.kpn.falcon.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors

/**
 * Section header — bold 16sp, left-aligned, no background.
 * Design spec: used as section titles within screen areas.
 */
@Composable
fun KPNSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = KPNColors.TextPrimary
        )
        trailing?.invoke()
    }
}

/**
 * Screen header bar — title + optional right action.
 */
@Composable
fun KPNTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = KPNColors.TextPrimary
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                KPNBackButton(onClick = onNavigateBack)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = KPNColors.Surface,
            titleContentColor = KPNColors.TextPrimary,
            actionIconContentColor = KPNColors.AccentOrange
        )
    )
}

/**
 * Collapsible section card — wraps any content under a toggle header.
 */
@Composable
fun KPNCollapsibleSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    editAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onToggle,
                colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.TextPrimary)
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(4.dp))
                Text(if (isExpanded) "▲" else "▼", style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
            }
            if (editAction != null) {
                TextButton(
                    onClick = editAction,
                    colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.AccentOrange)
                ) {
                    Text("Edit", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
        if (isExpanded) {
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

/**
 * Key-value pair row used in detail sections (Store Specs, Commercials, etc.)
 */
@Composable
fun KPNKeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = KPNColors.TextPrimary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = KPNColors.TextSecondary,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = valueColor,
            modifier = Modifier.weight(0.55f)
        )
    }
}
