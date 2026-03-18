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
    background = KPNColors.Background,
    surface = KPNColors.Surface,
    onBackground = KPNColors.TextPrimary,
    onSurface = KPNColors.TextPrimary,
    error = KPNColors.AccentRed,
    outline = KPNColors.Border
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
    )
)

@Composable
fun KPNTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KPNColorScheme,
        typography = KPNTypography,
        content = content
    )
}
