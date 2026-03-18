package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.util.Strings

/**
 * Full-screen loading state — KPN yellow spinner, centered.
 */
@Composable
fun KPNLoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = KPNColors.Primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(44.dp)
        )
    }
}

/**
 * Inline loading indicator (smaller, used within cards).
 */
@Composable
fun KPNLoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = KPNColors.Primary,
        strokeWidth = 2.dp,
        modifier = modifier.size(24.dp)
    )
}

/**
 * Full-screen error state with retry button.
 */
@Composable
fun KPNErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = KPNColors.AccentRed,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineMedium,
            color = KPNColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = KPNColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        KPNPrimaryButton(
            text = Strings.RETRY,
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}

/**
 * Empty state — illustration placeholder + title + subtitle.
 */
@Composable
fun KPNEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Illustration placeholder — replaced with actual asset in Task 4
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(color = KPNColors.Background, shape = KPNRadius.card),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = KPNColors.Border,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = KPNColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = KPNColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        if (action != null) {
            Spacer(Modifier.height(24.dp))
            action()
        }
    }
}

/**
 * Top banner shown when device is offline.
 * Design spec: shown across all screens when NetworkMonitor.isConnected = false.
 */
@Composable
fun OfflineBanner(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(KPNColors.AccentOrange)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.WifiOff,
            contentDescription = null,
            tint = KPNColors.Surface,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = Strings.OFFLINE_BANNER,
            style = MaterialTheme.typography.labelMedium,
            color = KPNColors.Surface
        )
    }
}

/**
 * Full-screen success state — used after Add Property wizard submission.
 * Design spec: green checkmark + "Submitted" label + yellow Done button.
 */
@Composable
fun KPNSuccessScreen(
    title: String = Strings.SUBMIT_SUCCESS,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KPNColors.Surface)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(color = KPNColors.AccentGreen.copy(alpha = 0.12f), shape = KPNRadius.chip),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = KPNColors.AccentGreen,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = KPNColors.AccentGreen,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Your property has been submitted for review.",
            style = MaterialTheme.typography.bodyLarge,
            color = KPNColors.TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        KPNPrimaryButton(
            text = Strings.DONE,
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

/**
 * Delete confirmation bottom sheet.
 * Design spec: always shows property ID before hard/soft delete.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KPNDeleteConfirmSheet(
    propertyId: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = KPNRadius.bottomSheet,
        containerColor = KPNColors.Surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = KPNColors.AccentRed,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = Strings.DELETE_CONFIRM_TITLE,
                style = MaterialTheme.typography.headlineMedium,
                color = KPNColors.TextPrimary
            )
            Text(
                text = Strings.DELETE_CONFIRM_MESSAGE.replace("%s", propertyId),
                style = MaterialTheme.typography.bodyLarge,
                color = KPNColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = KPNRadius.ctaButton,
                colors = ButtonDefaults.buttonColors(containerColor = KPNColors.AccentRed)
            ) {
                Text(Strings.DELETE_CONFIRM_CTA, color = KPNColors.Surface, style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text(Strings.DELETE_CANCEL, color = KPNColors.TextSecondary, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
