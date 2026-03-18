package com.kpn.falcon.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.kpn.falcon.data.models.CompetitorPresence
import com.kpn.falcon.data.models.GeoIQData
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNElevation
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.viewmodels.PropertyDetailUiState
import com.kpn.falcon.presentation.viewmodels.PropertyDetailViewModel

@Composable
fun GeoIQTab(
    state: PropertyDetailUiState,
    viewModel: PropertyDetailViewModel
) {
    val geoIq = state.property?.geoIq

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── SLA countdown banner ──────────────────────
        item {
            SLABanner(
                hoursRemaining = state.slaHoursRemaining,
                isBreached = state.slaIsBreached,
                deadline = state.slaDeadline
            )
        }

        // ── Upload error ──────────────────────────────
        if (state.geoIqUploadError != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(KPNColors.AccentRed.copy(alpha = 0.08f), KPNRadius.card)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.geoIqUploadError,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KPNColors.AccentRed,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = viewModel::dismissGeoIqError, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = KPNColors.AccentRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // ── PDF upload section ────────────────────────
        item {
            GeoIQUploadCard(
                hasPdf = geoIq?.pdfUrl != null,
                isParsed = geoIq?.parsedAt != null,
                isUploading = state.geoIqUploading,
                pdfUrl = geoIq?.pdfUrl,
                onUpload = viewModel::uploadGeoIQPdf
            )
        }

        // ── Parsed data (only when available) ────────
        if (geoIq != null && geoIq.parsedAt != null) {

            // Overall rating card
            item {
                GeoIQRatingCard(geoIq = geoIq)
            }

            // Catchment section
            item {
                GeoIQSection(title = "Catchment — Household Data") {
                    GeoIQCatchmentContent(geoIq = geoIq)
                }
            }

            // Market insights section
            item {
                GeoIQSection(title = "Market Insights") {
                    GeoIQMarketContent(geoIq = geoIq)
                }
            }

            // Competition section
            item {
                GeoIQSection(title = "Competition Analysis") {
                    GeoIQCompetitionContent(geoIq = geoIq)
                }
            }

            // KPN proximity
            if (geoIq.nearestKpnStore != null) {
                item {
                    GeoIQSection(title = "KPN Proximity") {
                        val isWithin3km = (geoIq.nearestKpnDistanceKm ?: Float.MAX_VALUE) <= 3f
                        KPNProximityBanner(
                            nearestStoreName = geoIq.nearestKpnStore,
                            distanceKm = geoIq.nearestKpnDistanceKm?.toDouble() ?: 0.0,
                            isWithin3km = isWithin3km
                        )
                    }
                }
            }
        }

        // ── Empty state (no parse yet) ────────────────
        if (geoIq == null || geoIq.parsedAt == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = KPNColors.TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "No GeoIQ data yet",
                            style = MaterialTheme.typography.titleSmall,
                            color = KPNColors.TextSecondary
                        )
                        Text(
                            "Upload the GeoIQ PDF above to auto-parse catchment and market data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = KPNColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// SLA banner
// ─────────────────────────────────────────────────

@Composable
private fun SLABanner(
    hoursRemaining: Long,
    isBreached: Boolean,
    deadline: Long
) {
    val bg = if (isBreached) KPNColors.AccentRed.copy(alpha = 0.08f) else KPNColors.AccentGreen.copy(alpha = 0.08f)
    val fg = if (isBreached) KPNColors.AccentRed else KPNColors.AccentGreen
    val icon = if (isBreached) Icons.Default.Warning else Icons.Default.Schedule

    val label = when {
        isBreached -> "SLA Breached — GeoIQ upload overdue"
        hoursRemaining < 24L -> "${hoursRemaining}h remaining to upload GeoIQ"
        else -> "${hoursRemaining / 24}d ${hoursRemaining % 24}h remaining to upload GeoIQ"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(bg, KPNRadius.card)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = fg)
        }
        SLACountdownChip(hoursRemaining = hoursRemaining, isBreached = isBreached)
    }
}

// ─────────────────────────────────────────────────
// PDF upload card
// ─────────────────────────────────────────────────

@Composable
private fun GeoIQUploadCard(
    hasPdf: Boolean,
    isParsed: Boolean,
    isUploading: Boolean,
    pdfUrl: String?,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
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
                Text(
                    "GeoIQ Report PDF",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = KPNColors.TextPrimary
                )
                if (isParsed) {
                    AutoParsedChip()
                }
            }

            when {
                isUploading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Uploading & parsing PDF…", style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextSecondary)
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = KPNColors.AccentGreen,
                            trackColor = KPNColors.Border
                        )
                    }
                }
                isParsed -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KPNColors.AccentGreen, modifier = Modifier.size(18.dp))
                        Text("PDF parsed successfully", style = MaterialTheme.typography.bodyMedium, color = KPNColors.AccentGreen)
                    }
                    // Re-upload option
                    TextButton(onClick = onUpload, colors = ButtonDefaults.textButtonColors(contentColor = KPNColors.AccentOrange)) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Re-upload PDF", style = MaterialTheme.typography.labelMedium)
                    }
                }
                hasPdf -> {
                    // Has PDF but not parsed
                    Text("PDF uploaded — awaiting parse", style = MaterialTheme.typography.bodyMedium, color = KPNColors.AccentOrange)
                    KPNPrimaryButton(text = "Upload GeoIQ PDF", onClick = onUpload)
                }
                else -> {
                    Text(
                        "Upload the GeoIQ report PDF to auto-extract catchment, market, and competition data.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KPNColors.TextSecondary
                    )
                    KPNPrimaryButton(
                        text = "Upload GeoIQ PDF",
                        onClick = onUpload,
                        icon = Icons.Default.Upload
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Overall rating card
// ─────────────────────────────────────────────────

@Composable
private fun GeoIQRatingCard(geoIq: GeoIQData) {
    val rating = geoIq.geoIqOverallRating
    val ratingColor = when {
        rating == null -> KPNColors.TextSecondary
        rating >= 7f -> KPNColors.AccentGreen
        rating >= 5f -> KPNColors.AccentOrange
        else -> KPNColors.AccentRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "GeoIQ Overall Rating",
                    style = MaterialTheme.typography.labelMedium,
                    color = KPNColors.TextSecondary
                )
                Text(
                    text = if (rating != null) "${formatFloat1dp(rating)} / 10" else "—",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ratingColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Circular score indicator
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(ratingColor.copy(alpha = 0.12f), KPNRadius.chip)
                    .border(2.dp, ratingColor, KPNRadius.chip),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (rating != null) formatFloat1dp(rating) else "—",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ratingColor
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Catchment content
// ─────────────────────────────────────────────────

@Composable
private fun GeoIQCatchmentContent(geoIq: GeoIQData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CatchmentBlock(label = "Within 1km radius", total = geoIq.hhTotal1km, above5L = geoIq.hhAbove5L1km, above10L = geoIq.hhAbove10L1km, above20L = geoIq.hhAbove20L1km)
        HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
        CatchmentBlock(label = "Within 10-min walk", total = geoIq.hhTotal10minWalk, above5L = geoIq.hhAbove5L10min, above10L = geoIq.hhAbove10L10min, above20L = geoIq.hhAbove20L10min)
        HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
        if (geoIq.hhTotal2km != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total HH within 2km", style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextSecondary)
                Text("${geoIq.hhTotal2km}", style = MaterialTheme.typography.bodyLarge, color = KPNColors.TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CatchmentBlock(label: String, total: Int?, above5L: Int?, above10L: Int?, above20L: Int?) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = KPNColors.AccentGreen, fontWeight = FontWeight.SemiBold)
        if (total != null) KPNKeyValueRow("Total Households", "$total")
        if (above5L != null) KPNKeyValueRow("HH above ₹5L", "$above5L")
        if (above10L != null) KPNKeyValueRow("HH above ₹10L", "$above10L")
        if (above20L != null) KPNKeyValueRow("HH above ₹20L", "$above20L")
    }
}

// ─────────────────────────────────────────────────
// Market insights content
// ─────────────────────────────────────────────────

@Composable
private fun GeoIQMarketContent(geoIq: GeoIQData) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        if (geoIq.affluenceIndex != null) KPNKeyValueRow("Affluence Index", formatFloat2dp(geoIq.affluenceIndex))
        if (geoIq.avgResidentialRentPerSqft != null) KPNKeyValueRow("Avg Residential Rent", "₹${formatFloat1dp(geoIq.avgResidentialRentPerSqft)}/sqft")
        if (geoIq.totalFootfallScore != null) KPNKeyValueRow("Footfall Score", formatFloat1dp(geoIq.totalFootfallScore))
        if (geoIq.growthTrendScore != null) KPNKeyValueRow("Growth Trend", formatFloat1dp(geoIq.growthTrendScore))
        if (geoIq.spendingCapacity != null) KPNKeyValueRow("Spending Capacity", formatFloat1dp(geoIq.spendingCapacity))
        if (geoIq.retailIndex != null) KPNKeyValueRow("Retail Index", formatFloat1dp(geoIq.retailIndex))
    }
}

// ─────────────────────────────────────────────────
// Competition content
// ─────────────────────────────────────────────────

@Composable
private fun GeoIQCompetitionContent(geoIq: GeoIQData) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (geoIq.competitorPresence != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Competitor Presence", style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextSecondary, modifier = Modifier.weight(0.45f))
                CompetitorPresencePill(presence = geoIq.competitorPresence)
            }
        }
        if (geoIq.cannibalisation != null) KPNKeyValueRow("Cannibalisation Risk", "${formatFloat1dp(geoIq.cannibalisation)}%")
        if (geoIq.storesInArea != null) KPNKeyValueRow("Stores in Area", "${geoIq.storesInArea}")

        if (geoIq.complementaryBrands.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Complementary Brands", style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(geoIq.complementaryBrands) { brand ->
                        Box(
                            modifier = Modifier
                                .background(KPNColors.Primary.copy(alpha = 0.15f), KPNRadius.chip)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(brand, style = MaterialTheme.typography.labelSmall, color = KPNColors.PrimaryText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompetitorPresencePill(presence: CompetitorPresence) {
    val (bg, fg, label) = when (presence) {
        CompetitorPresence.HIGH -> Triple(KPNColors.AccentRed.copy(alpha = 0.12f), KPNColors.AccentRed, "HIGH")
        CompetitorPresence.MEDIUM -> Triple(KPNColors.AccentOrange.copy(alpha = 0.12f), KPNColors.AccentOrange, "MEDIUM")
        CompetitorPresence.LOW -> Triple(KPNColors.AccentGreen.copy(alpha = 0.12f), KPNColors.AccentGreen, "LOW")
    }
    Box(
        modifier = Modifier
            .background(bg, KPNRadius.chip)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = fg, fontWeight = FontWeight.Bold)
    }
}

// ─────────────────────────────────────────────────
// Section card wrapper
// ─────────────────────────────────────────────────

@Composable
private fun GeoIQSection(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            KPNCollapsibleSection(
                title = title,
                isExpanded = expanded,
                onToggle = { expanded = !expanded }
            ) {
                content()
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Number formatters (KMP-safe — no String.format)
// ─────────────────────────────────────────────────

private fun formatFloat1dp(value: Float): String {
    val scaled = (value * 10).toInt()
    val whole = scaled / 10
    val frac = scaled % 10
    return "$whole.$frac"
}

private fun formatFloat2dp(value: Float): String {
    val scaled = (value * 100).toInt()
    val whole = scaled / 100
    val frac = scaled % 100
    val fracStr = if (frac < 10) "0$frac" else "$frac"
    return "$whole.$fracStr"
}
