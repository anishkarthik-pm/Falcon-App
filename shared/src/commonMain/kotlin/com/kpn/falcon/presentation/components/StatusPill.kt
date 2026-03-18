package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kpn.falcon.data.models.PropertyStatus
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius

/**
 * Rounded status pill with dot prefix.
 * Design spec: colored background, dot prefix, 11sp label.
 */
@Composable
fun StatusPill(
    status: PropertyStatus,
    modifier: Modifier = Modifier
) {
    val (label, bg, fg) = statusStyle(status)

    Row(
        modifier = modifier
            .background(color = bg.copy(alpha = 0.15f), shape = KPNRadius.statusPill)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color = fg, shape = CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg
        )
    }
}

private data class StatusStyle(val label: String, val background: Color, val foreground: Color)

private fun statusStyle(status: PropertyStatus): StatusStyle = when (status) {
    PropertyStatus.DRAFT -> StatusStyle("Draft", KPNColors.StatusDraft, KPNColors.StatusDraft)
    PropertyStatus.SUBMITTED -> StatusStyle("Submitted", KPNColors.StatusSubmitted, KPNColors.StatusSubmitted)
    PropertyStatus.IN_REVIEW -> StatusStyle("In Review", KPNColors.StatusPending, KPNColors.StatusPending)
    PropertyStatus.FORWARDED -> StatusStyle("Forwarded", KPNColors.StatusPending, KPNColors.StatusPending)
    PropertyStatus.APPROVED -> StatusStyle("Approved", KPNColors.StatusApproved, KPNColors.StatusApproved)
    PropertyStatus.REJECTED -> StatusStyle("Rejected", KPNColors.StatusRejected, KPNColors.StatusRejected)
}
