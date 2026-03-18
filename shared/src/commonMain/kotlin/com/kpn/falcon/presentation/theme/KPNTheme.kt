package com.kpn.falcon.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val KPNColorScheme = lightColorScheme(
    primary = KPNColors.Primary,
    onPrimary = KPNColors.PrimaryText,
    secondary = KPNColors.AccentOrange,
    onSecondary = KPNColors.Surface,
    tertiary = KPNColors.AccentGreen,
    onTertiary = KPNColors.Surface,
    background = KPNColors.Background,
    onBackground = KPNColors.TextPrimary,
    surface = KPNColors.Surface,
    onSurface = KPNColors.TextPrimary,
    surfaceVariant = KPNColors.Background,
    onSurfaceVariant = KPNColors.TextSecondary,
    error = KPNColors.AccentRed,
    onError = KPNColors.Surface,
    outline = KPNColors.Border,
    outlineVariant = KPNColors.Border,
    scrim = KPNColors.TextPrimary.copy(alpha = 0.32f)
)

val KPNTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = KPNColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = KPNColors.TextPrimary
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = KPNColors.TextPrimary
    ),
    bodyLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = KPNColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        color = KPNColors.TextSecondary
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = KPNColors.TextSecondary
    ),
    // Extra styles used throughout the app
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = KPNColors.TextPrimary
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = KPNColors.TextPrimary
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = KPNColors.TextSecondary
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = KPNColors.TextPrimary
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = KPNColors.TextPrimary
    )
)

@Composable
fun KPNTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KPNColorScheme,
        typography = KPNTypography,
        shapes = KPNShapes,
        content = content
    )
}
