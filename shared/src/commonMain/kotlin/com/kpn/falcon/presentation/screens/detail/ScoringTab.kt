package com.kpn.falcon.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpn.falcon.domain.usecase.SuggestedScore
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNElevation
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.viewmodels.PropertyDetailUiState
import com.kpn.falcon.presentation.viewmodels.PropertyDetailViewModel
import com.kpn.falcon.util.KPNConstants

// ─────────────────────────────────────────────────
// Scoring param metadata
// ─────────────────────────────────────────────────

private data class ScoringParam(
    val key: String,
    val label: String,
    val weight: Float,
    val unit: String
)

private val SCORING_PARAMS = listOf(
    ScoringParam("area", "Store Area", KPNConstants.WEIGHT_AREA, "sqft"),
    ScoringParam("frontage", "Store Frontage", KPNConstants.WEIGHT_FRONTAGE, "ft"),
    ScoringParam("carParking", "Car Parking", KPNConstants.WEIGHT_CAR_PARKING, "spots"),
    ScoringParam("bikeParking", "Bike Parking", KPNConstants.WEIGHT_BIKE_PARKING, "spots"),
    ScoringParam("juiceCounter", "Juice Counter", KPNConstants.WEIGHT_JUICE_COUNTER, ""),
    ScoringParam("stepsToEntry", "Steps to Entry", KPNConstants.WEIGHT_STEPS_TO_ENTRY, "steps"),
    ScoringParam("roadWidth", "Road Width", KPNConstants.WEIGHT_ROAD_WIDTH, "ft")
)

// ─────────────────────────────────────────────────
// Main composable
// ─────────────────────────────────────────────────

@Composable
fun ScoringTab(
    state: PropertyDetailUiState,
    viewModel: PropertyDetailViewModel
) {
    var salesProjection by remember { mutableStateOf(state.property?.scoring?.salesProjection?.toString() ?: "") }
    var comparableRef by remember { mutableStateOf(state.property?.scoring?.comparableStoreRef ?: "") }
    var strategicNotes by remember { mutableStateOf(state.property?.scoring?.strategicNotes ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Composite score card ──────────────────────
        item {
            CompositeScoreCard(
                score = state.compositeScore,
                confirmedByStateHead = state.property?.scoring?.confirmedByStateHead ?: false
            )
        }

        // ── Error banner ──────────────────────────────
        if (state.scoringError != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(KPNColors.AccentRed.copy(alpha = 0.08f), KPNRadius.card)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = KPNColors.AccentRed, modifier = Modifier.size(16.dp))
                    Text(state.scoringError, style = MaterialTheme.typography.bodyMedium, color = KPNColors.AccentRed)
                }
            }
        }

        // ── Scoring rows section header ───────────────
        item {
            SectionCardHeader(title = "Parameter Scores", subtitle = "Tap dots to override auto-suggested scores")
        }

        // ── Individual parameter rows ─────────────────
        items(SCORING_PARAMS) { param ->
            val suggestion = state.suggestedScores.find { it.parameter == param.key }
            val currentScore = state.currentScores[param.key] ?: 0

            ScoringParamRow(
                param = param,
                suggestion = suggestion,
                currentScore = currentScore,
                onScoreChange = { viewModel.updateScore(param.key, it) }
            )
        }

        // ── Supplementary fields ──────────────────────
        item {
            SectionCardHeader(title = "Projection & Notes")
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = KPNRadius.card,
                colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
                elevation = CardDefaults.cardElevation(KPNElevation.card)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KPNNumberField(
                        value = salesProjection,
                        onValueChange = { salesProjection = it },
                        label = "Sales Projection",
                        suffix = "₹/month"
                    )
                    KPNTextField(
                        value = comparableRef,
                        onValueChange = { comparableRef = it },
                        label = "Comparable Store Reference",
                        hint = "e.g. KPN-TN-2024-001"
                    )
                    KPNTextField(
                        value = strategicNotes,
                        onValueChange = { strategicNotes = it },
                        label = "Strategic Notes",
                        hint = "Any additional observations or strategic context",
                        minLines = 3
                    )
                }
            }
        }

        // ── Save button ───────────────────────────────
        item {
            Spacer(Modifier.height(4.dp))
            KPNPrimaryButton(
                text = if (state.scoringSaved) "Saved ✓" else "Save Scoring",
                onClick = {
                    viewModel.saveScoring(
                        salesProjection = salesProjection.toLongOrNull(),
                        comparableRef = comparableRef.ifBlank { null },
                        strategicNotes = strategicNotes.ifBlank { null }
                    )
                },
                isLoading = state.scoringSaving,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // ── State Head confirmation ───────────────────
        val scoring = state.property?.scoring
        if (scoring != null) {
            item {
                StateHeadConfirmCard(
                    isConfirmed = scoring.confirmedByStateHead,
                    onConfirm = viewModel::confirmScoringAsStateHead
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Composite score card
// ─────────────────────────────────────────────────

@Composable
private fun CompositeScoreCard(score: Float?, confirmedByStateHead: Boolean) {
    val maxScore = 5f
    val progress = ((score ?: 0f) / maxScore).coerceIn(0f, 1f)
    val scoreColor = when {
        score == null -> KPNColors.TextSecondary
        score >= 4f -> KPNColors.AccentGreen
        score >= 2.5f -> KPNColors.AccentOrange
        else -> KPNColors.AccentRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Composite Score", style = MaterialTheme.typography.labelMedium, color = KPNColors.TextSecondary)
                    Text(
                        text = if (score != null) "${formatScore(score)} / 5.0" else "—",
                        style = MaterialTheme.typography.headlineMedium,
                        color = scoreColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Circle badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(scoreColor.copy(alpha = 0.10f), KPNRadius.chip)
                        .border(3.dp, scoreColor, KPNRadius.chip),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (score != null) formatScore(score) else "—",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = scoreColor
                    )
                }
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = scoreColor,
                trackColor = KPNColors.Border
            )

            // Weights breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Weights:", style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
                Text(
                    text = "Area 30% · Frontage 20% · Parking 10%×2 · Counter 10% · Steps 10% · Road 10%",
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.TextSecondary
                )
            }

            if (confirmedByStateHead) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KPNColors.AccentGreen, modifier = Modifier.size(14.dp))
                    Text("Confirmed by State Head", style = MaterialTheme.typography.labelSmall, color = KPNColors.AccentGreen)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Individual parameter row
// ─────────────────────────────────────────────────

@Composable
private fun ScoringParamRow(
    param: ScoringParam,
    suggestion: SuggestedScore?,
    currentScore: Int,
    onScoreChange: (Int) -> Unit
) {
    val suggestedScore = suggestion?.suggestedScore
    val isOverridden = suggestedScore != null && currentScore != suggestedScore && currentScore > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(param.label, style = MaterialTheme.typography.titleSmall, color = KPNColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Weight: ${(param.weight * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = KPNColors.TextSecondary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Auto-suggest badge
                    if (suggestedScore != null) {
                        Box(
                            modifier = Modifier
                                .background(KPNColors.Primary.copy(alpha = 0.15f), KPNRadius.chip)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "⚡ $suggestedScore",
                                style = MaterialTheme.typography.labelSmall,
                                color = KPNColors.AccentOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    // Override indicator
                    if (isOverridden) {
                        Icon(Icons.Default.Edit, contentDescription = "Overridden", tint = KPNColors.AccentOrange, modifier = Modifier.size(12.dp))
                    }
                }
            }

            // Raw value info
            if (suggestion != null && suggestion.lookupTable.isNotEmpty()) {
                Text(
                    text = "Raw: ${suggestion.rawValue}${param.unit.let { if (it.isNotEmpty()) " $it" else "" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.TextSecondary
                )
            }

            // Score selector (1-5 dot buttons)
            ScoreDotSelector(
                current = currentScore,
                suggested = suggestedScore,
                onSelect = onScoreChange
            )
        }
    }
}

@Composable
private fun ScoreDotSelector(
    current: Int,
    suggested: Int?,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (score in 1..5) {
            val isSelected = score == current
            val isSuggested = score == suggested
            val bgColor = when {
                isSelected -> KPNColors.AccentGreen
                isSuggested -> KPNColors.Primary
                else -> KPNColors.Border
            }
            val textColor = when {
                isSelected -> KPNColors.Surface
                isSuggested -> KPNColors.PrimaryText
                else -> KPNColors.TextSecondary
            }

            Box(
                modifier = Modifier
                    .size(if (isSelected) 40.dp else 36.dp)
                    .background(bgColor, KPNRadius.chip)
                    .clickable { onSelect(score) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.labelLarge,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        // Clear / reset
        IconButton(
            onClick = { onSelect(suggested ?: 0) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset to suggested", tint = KPNColors.TextSecondary, modifier = Modifier.size(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────
// State Head confirmation card
// ─────────────────────────────────────────────────

@Composable
private fun StateHeadConfirmCard(
    isConfirmed: Boolean,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(
            containerColor = if (isConfirmed) KPNColors.AccentGreen.copy(alpha = 0.06f) else KPNColors.Surface
        ),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "State Head Confirmation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KPNColors.TextPrimary
                )
                Text(
                    text = if (isConfirmed) "Scoring confirmed ✓" else "Confirm that scoring accurately reflects the property",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isConfirmed) KPNColors.AccentGreen else KPNColors.TextSecondary
                )
            }

            if (!isConfirmed) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KPNColors.AccentGreen,
                        contentColor = KPNColors.Surface
                    ),
                    shape = KPNRadius.ctaButton
                ) {
                    Text("Confirm", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KPNColors.AccentGreen, modifier = Modifier.size(28.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Section header helper
// ─────────────────────────────────────────────────

@Composable
private fun SectionCardHeader(title: String, subtitle: String? = null) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.TextPrimary)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
        }
    }
}

// ─────────────────────────────────────────────────
// Number formatter
// ─────────────────────────────────────────────────

private fun formatScore(value: Float): String {
    val scaled = (value * 10).toInt()
    val whole = scaled / 10
    val frac = scaled % 10
    return "$whole.$frac"
}
