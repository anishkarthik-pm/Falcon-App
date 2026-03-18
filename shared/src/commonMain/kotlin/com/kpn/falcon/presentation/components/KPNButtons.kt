package com.kpn.falcon.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.util.Strings

/**
 * Primary CTA button — full-width, yellow background, black text, 28dp corner.
 * Design spec: the main action button used throughout the wizard and submit flows.
 */
@Composable
fun KPNPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = KPNRadius.ctaButton,
        colors = ButtonDefaults.buttonColors(
            containerColor = KPNColors.Primary,
            contentColor = KPNColors.PrimaryText,
            disabledContainerColor = KPNColors.Primary.copy(alpha = 0.4f),
            disabledContentColor = KPNColors.PrimaryText.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = KPNColors.PrimaryText,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/**
 * Back / Cancel text button — orange text, left-aligned.
 * Design spec: text button, AccentOrange color.
 */
@Composable
fun KPNBackButton(
    text: String = Strings.BACK,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.ArrowBack
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.AccentOrange)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Orange outlined action button (e.g. "+ Add Property", "Capture GPS Location").
 */
@Composable
fun KPNOutlinedActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = KPNRadius.ctaButton,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = KPNColors.AccentOrange),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(KPNColors.AccentOrange)
        )
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Icon text button used for quick-action rows on Home screen (+ Add, ⊞ Kanban).
 */
@Composable
fun KPNTextActionButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.AccentOrange)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Wizard nav row — Back (left) + Next (right).
 */
@Composable
fun WizardNavRow(
    onBack: (() -> Unit)?,
    onNext: () -> Unit,
    nextLabel: String = Strings.NEXT,
    nextEnabled: Boolean = true,
    isNextLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            KPNBackButton(onClick = onBack)
        } else {
            Spacer(Modifier.width(80.dp))
        }
        KPNPrimaryButton(
            text = nextLabel,
            onClick = onNext,
            enabled = nextEnabled,
            isLoading = isNextLoading,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}
