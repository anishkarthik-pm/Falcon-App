package com.kpn.falcon.presentation.screens.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kpn.falcon.data.models.Competitor
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.viewmodels.AddPropertyUiState
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.Strings

@Composable
fun Step6CompetitionContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val competitors = state.competitors
    val errors = state.validationErrors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(title = Strings.COMPETITION_HEADER, fieldProgress = state.stepProgress)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Empty state ───────────────────────────────
            if (competitors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KPNColors.Surface, KPNRadius.card)
                        .border(1.dp, KPNColors.Border, KPNRadius.card)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = Strings.COMPETITION_EMPTY,
                            style = MaterialTheme.typography.bodyMedium,
                            color = KPNColors.TextSecondary
                        )
                        Text(
                            text = Strings.COMPETITION_EMPTY_HINT,
                            style = MaterialTheme.typography.labelSmall,
                            color = KPNColors.TextSecondary
                        )
                    }
                }
            }

            // ── Competitor cards ──────────────────────────
            competitors.forEachIndexed { index, competitor ->
                CompetitorCard(
                    competitor = competitor,
                    index = index,
                    errors = errors,
                    onUpdate = { viewModel.updateCompetitor(index, it) },
                    onDelete = { viewModel.deleteCompetitor(index) }
                )
            }

            // ── Add competitor button ─────────────────────
            OutlinedButton(
                onClick = { viewModel.addCompetitor() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KPNColors.Primary),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, KPNColors.Primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(Strings.COMPETITION_ADD_COMPETITOR)
            }

            // Info note
            if (competitors.isNotEmpty()) {
                Text(
                    text = Strings.COMPETITION_NOTE,
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.TextSecondary
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CompetitorCard(
    competitor: Competitor,
    index: Int,
    errors: Map<String, String>,
    onUpdate: (Competitor) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val brandError = errors["competitor_${index}_brand"]
    val distanceError = errors["competitor_${index}_distance"]

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = KPNElevation.card)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // ── Card header ───────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(KPNColors.Primary, KPNRadius.chip),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = KPNColors.PrimaryText
                        )
                    }
                    Text(
                        text = competitor.brandName.ifBlank { "Competitor ${index + 1}" },
                        style = MaterialTheme.typography.titleSmall,
                        color = KPNColors.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = KPNColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete competitor",
                            tint = KPNColors.AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── Card body (expandable) ────────────────────
            if (expanded) {
                // Brand + Distance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KPNTextField(
                        value = competitor.brandName,
                        onValueChange = { onUpdate(competitor.copy(brandName = it)) },
                        label = Strings.COMPETITION_BRAND_NAME,
                        isRequired = true,
                        isError = brandError != null,
                        modifier = Modifier.weight(1f)
                    )
                    KPNNumberField(
                        value = if (competitor.distanceMeters > 0) competitor.distanceMeters.toString() else "",
                        onValueChange = { onUpdate(competitor.copy(distanceMeters = it.toIntOrNull() ?: 0)) },
                        label = Strings.COMPETITION_DISTANCE,
                        suffix = "m",
                        isRequired = true,
                        isError = distanceError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (brandError != null) {
                    Text(brandError, style = MaterialTheme.typography.labelSmall, color = KPNColors.AccentRed)
                }

                // Area + Rent
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KPNNumberField(
                        value = if (competitor.storeAreaSqft > 0) competitor.storeAreaSqft.toString() else "",
                        onValueChange = { onUpdate(competitor.copy(storeAreaSqft = it.toIntOrNull() ?: 0)) },
                        label = Strings.COMPETITION_STORE_AREA,
                        suffix = "sqft",
                        modifier = Modifier.weight(1f)
                    )
                    KPNNumberField(
                        value = competitor.rentPerSqft?.toString() ?: "",
                        onValueChange = { onUpdate(competitor.copy(rentPerSqft = it.toFloatOrNull())) },
                        label = Strings.COMPETITION_RENT_SQFT,
                        suffix = "₹/sqft",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Monthly sales
                KPNNumberField(
                    value = competitor.salesPerMonth?.toString() ?: "",
                    onValueChange = { onUpdate(competitor.copy(salesPerMonth = it.toLongOrNull())) },
                    label = Strings.COMPETITION_SALES,
                    suffix = "₹/month"
                )

                // Photos count indicator (upload handled via media step)
                if (competitor.photos.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${competitor.photos.size} photo(s) attached",
                            style = MaterialTheme.typography.labelSmall,
                            color = KPNColors.AccentGreen
                        )
                    }
                }
            }
        }
    }
}
