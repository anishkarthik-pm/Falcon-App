package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius

// ─────────────────────────────────────────────────
// Wizard header
// ─────────────────────────────────────────────────

/**
 * Add Property wizard header.
 * Design spec: ✕ close icon left, "Add New Property" title, step counter chip top-right.
 */
@Composable
fun WizardHeader(
    currentStep: Int,
    totalSteps: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(KPNColors.Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = KPNColors.AccentOrange,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onClose)
            )
            Text(
                text = "Add New Property",
                style = MaterialTheme.typography.headlineMedium,
                color = KPNColors.TextPrimary
            )
        }
        // Step counter chip
        Box(
            modifier = Modifier
                .background(KPNColors.Background, KPNRadius.chip)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$currentStep / $totalSteps",
                style = MaterialTheme.typography.labelMedium,
                color = KPNColors.TextSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────
// Wizard tab row
// ─────────────────────────────────────────────────

/**
 * Horizontal scrollable tab row for wizard steps.
 * Design spec: active tab has green underline, text is 14sp.
 */
@Composable
fun WizardTabRow(
    tabs: List<String>,
    currentStep: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(currentStep) {
        listState.animateScrollToItem((currentStep - 1).coerceAtLeast(0))
    }

    Column(modifier = modifier) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .background(KPNColors.Surface),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            itemsIndexed(tabs) { index, tab ->
                val stepNumber = index + 1
                val isActive = stepNumber == currentStep
                val isComplete = stepNumber < currentStep

                WizardTab(
                    label = tab,
                    isActive = isActive,
                    isComplete = isComplete,
                    onClick = { onTabSelected(stepNumber) }
                )
            }
        }
        HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
    }
}

@Composable
private fun WizardTab(
    label: String,
    isActive: Boolean,
    isComplete: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                isActive -> KPNColors.TextPrimary
                isComplete -> KPNColors.AccentGreen
                else -> KPNColors.TextSecondary
            },
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
        // Active underline
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(if (isActive) 40.dp else if (isComplete) 32.dp else 0.dp)
                .background(
                    color = if (isComplete) KPNColors.AccentGreen else KPNColors.AccentGreen,
                    shape = KPNRadius.chip
                )
        )
    }
}

// ─────────────────────────────────────────────────
// Step section header
// ─────────────────────────────────────────────────

/**
 * Step header with emoji title + thin green progress bar.
 * Design spec: shown at the top of each step's content area.
 */
@Composable
fun WizardStepHeader(
    title: String,
    fieldProgress: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KPNColors.Surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = KPNColors.TextSecondary
        )
        StepProgressBar(progress = fieldProgress)
    }
}

// ─────────────────────────────────────────────────
// Phase tracker (Property Detail — Overview tab)
// ─────────────────────────────────────────────────

/**
 * Phase tracker showing 8 numbered phases with connecting line.
 * Design spec: numbered dots 1–8, connected line,
 *              active dot filled green, current phase label.
 */
@Composable
fun PhaseTracker(
    currentPhase: Int,
    totalPhases: Int = 8,
    daysInStage: Int = 0,
    modifier: Modifier = Modifier
) {
    val phaseLabels = listOf(
        "Lead Gen", "Store Specs", "GeoIQ", "Scoring",
        "Commercials", "Legal", "Site Visit", "Signing"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Dot row with connecting line
        Box(modifier = Modifier.fillMaxWidth()) {
            // Connecting line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.Center)
                    .background(KPNColors.Border)
            )
            // Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (phase in 1..totalPhases) {
                    PhaseDot(phase = phase, currentPhase = currentPhase)
                }
            }
        }

        // Active phase label
        val activeLabel = phaseLabels.getOrElse(currentPhase - 1) { "Phase $currentPhase" }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activeLabel,
                style = MaterialTheme.typography.titleSmall,
                color = KPNColors.AccentGreen
            )
            if (daysInStage > 0) {
                Text(
                    text = "· $daysInStage day${if (daysInStage > 1) "s" else ""} in this stage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun PhaseDot(phase: Int, currentPhase: Int) {
    val isComplete = phase < currentPhase
    val isActive = phase == currentPhase

    val bgColor = when {
        isComplete -> KPNColors.AccentGreen
        isActive -> KPNColors.AccentGreen
        else -> KPNColors.Border
    }
    val textColor = when {
        isComplete || isActive -> KPNColors.Surface
        else -> KPNColors.TextSecondary
    }
    val size = if (isActive) 32.dp else 24.dp

    Box(
        modifier = Modifier
            .size(size)
            .background(bgColor, KPNRadius.chip),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = phase.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontSize = if (isActive) 13.sp else 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────
// Property Lead Details block (read-only, Step 1 top)
// ─────────────────────────────────────────────────

/**
 * Read-only property ID / BD Executive / Date Added block shown at top of Step 1.
 */
@Composable
fun PropertyLeadDetailsBlock(
    propertyId: String,
    bdExecutiveName: String,
    dateAdded: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Background),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            KPNKeyValueRow(
                label = "Property ID",
                value = propertyId,
                valueColor = if (propertyId == "Pending") KPNColors.TextSecondary else KPNColors.AccentGreen
            )
            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
            KPNKeyValueRow(label = "BD Executive", value = bdExecutiveName)
            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
            KPNKeyValueRow(label = "Date Added", value = dateAdded)
        }
    }
}

// ─────────────────────────────────────────────────
// SLA Countdown chip (GeoIQ tab)
// ─────────────────────────────────────────────────

/**
 * SLA countdown chip — green if OK, red if breached.
 */
@Composable
fun SLACountdownChip(
    hoursRemaining: Long,
    isBreached: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isBreached) KPNColors.AccentRed else KPNColors.AccentGreen
    val label = when {
        isBreached -> "SLA Breached"
        hoursRemaining < 24 -> "${hoursRemaining}h remaining"
        else -> "${hoursRemaining / 24}d remaining"
    }

    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), KPNRadius.chip)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.size(6.dp).background(color, KPNRadius.chip))
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
