package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BoltOutlined
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNElevation
import com.kpn.falcon.presentation.theme.KPNRadius

/**
 * Property list card.
 * Design spec: white surface, 8dp corner, 12dp padding, 2dp shadow.
 */
@Composable
fun PropertyCard(
    property: PropertyLead,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = KPNElevation.card)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Name + flash icon + arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = property.contact.landlordName.ifBlank { "Unnamed Property" },
                        style = MaterialTheme.typography.titleSmall,
                        color = KPNColors.TextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Recent activity",
                        tint = KPNColors.AccentOrange,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusPill(property.status)
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = KPNColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Row 2: Property ID
            Text(
                text = property.propertyId,
                style = MaterialTheme.typography.labelMedium,
                color = KPNColors.AccentGreen,
                fontWeight = FontWeight.SemiBold
            )

            // Row 3: Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = KPNColors.TextSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = buildString {
                        if (property.location.city.isNotBlank()) append(property.location.city)
                        if (property.location.city.isNotBlank() && property.location.state.isNotBlank()) append(", ")
                        if (property.location.state.isNotBlank()) append(property.location.state)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.TextSecondary
                )
            }

            // Row 4: Area + Rent metric chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip(
                    label = buildString {
                        append("Store Area: ")
                        append(property.storeSpecs.carpetArea.takeIf { it > 0 } ?: property.storeSpecs.totalArea)
                        append(" sqft")
                    }
                )
                if (property.commercials.landlordRentPerSqft > 0) {
                    MetricChip(label = "Rent: ₹${property.commercials.landlordRentPerSqft.toInt()}")
                }
            }

            // Row 5: BD Manager avatar + Created date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (property.bdManagerId != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AvatarChip(initials = "BM", name = "BD Mgr")
                    }
                }
                Text(
                    text = "Created: ${formatEpochToDate(property.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun MetricChip(label: String) {
    Box(
        modifier = Modifier
            .background(color = KPNColors.Background, shape = KPNRadius.chip)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = KPNColors.TextPrimary
        )
    }
}

@Composable
fun AvatarChip(
    initials: String,
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(KPNColors.Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = KPNColors.PrimaryText,
                fontSize = 9.sp
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = KPNColors.TextSecondary
        )
    }
}

private fun formatEpochToDate(epochMs: Long): String {
    if (epochMs == 0L) return "—"
    return epochMsToDateString(epochMs)
}
