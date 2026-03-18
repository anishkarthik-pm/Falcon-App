package com.kpn.falcon.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val KPNShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),       // Cards, inputs
    medium = RoundedCornerShape(12.dp),     // Dialogs, bottom sheets
    large = RoundedCornerShape(16.dp),      // Large cards
    extraLarge = RoundedCornerShape(28.dp)  // Primary CTA buttons
)

// Domain-specific shape constants
object KPNRadius {
    val card = RoundedCornerShape(8.dp)
    val input = RoundedCornerShape(8.dp)
    val chip = RoundedCornerShape(50)         // Full pill
    val ctaButton = RoundedCornerShape(28.dp) // Primary action button
    val statusPill = RoundedCornerShape(50)   // Status indicators
    val uploadSlot = RoundedCornerShape(8.dp)
    val bottomSheet = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
}
