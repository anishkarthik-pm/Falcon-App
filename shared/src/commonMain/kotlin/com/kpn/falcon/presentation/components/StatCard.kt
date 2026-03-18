package com.kpn.falcon.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNElevation
import com.kpn.falcon.presentation.theme.KPNRadius

/**
 * Dashboard stat card — icon, label, bold count, arrow.
 * Used in 2x2 grid on Home screen.
 */
@Composable
fun StatCard(
    label: String,
    count: Int,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = KPNElevation.card)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = KPNColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = KPNColors.TextPrimary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = KPNColors.TextSecondary
            )
        }
    }
}
