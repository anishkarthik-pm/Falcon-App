package com.kpn.falcon.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.util.Strings
import kotlin.math.round

// ─────────────────────────────────────────────────
// GPS Chip
// ─────────────────────────────────────────────────

/**
 * GPS capture button + captured state.
 * Design spec: button → "Capture GPS Location" (orange outlined)
 *              captured → "GPS Captured ✓ 19.226227, 72.842567" (green text, inline)
 */
@Composable
fun GPSCaptureField(
    lat: Double?,
    lng: Double?,
    isCapturing: Boolean,
    onCapture: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "GPS Location *",
            style = MaterialTheme.typography.bodyMedium,
            color = KPNColors.TextSecondary
        )
        if (lat != null && lng != null) {
            // Captured state
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KPNColors.AccentGreen.copy(alpha = 0.08f), KPNRadius.input)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = KPNColors.AccentGreen,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${Strings.LOCATION_GPS_CAPTURED} ${formatCoord(lat)}, ${formatCoord(lng)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KPNColors.AccentGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                // Allow re-capture
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Re-capture",
                    tint = KPNColors.AccentGreen,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(onClick = onCapture)
                )
            }
        } else {
            // Capture button
            KPNOutlinedActionButton(
                text = if (isCapturing) "Locating…" else Strings.LOCATION_CAPTURE_GPS,
                onClick = onCapture,
                icon = if (isCapturing) null else Icons.Default.MyLocation,
                enabled = !isCapturing,
                modifier = Modifier.fillMaxWidth()
            )
            if (isCapturing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = KPNColors.AccentGreen,
                    trackColor = KPNColors.Border
                )
            }
        }
    }
}

private fun formatCoord(value: Double): String {
    // Round to 6 decimal places without java.math (KMP compatible)
    val factor = 1_000_000.0
    val rounded = round(value * factor) / factor
    return rounded.toString()
}

// ─────────────────────────────────────────────────
// Deviation Flag
// ─────────────────────────────────────────────────

/**
 * Inline orange "Deviation from Std" / "Deviation from baseline" label.
 * Design spec: orange label shown below a field when it deviates from KPN standard.
 */
@Composable
fun DeviationFlag(
    message: String = Strings.DEVIATION_FROM_STD,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = KPNColors.AccentOrange,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = KPNColors.AccentOrange
        )
    }
}

// ─────────────────────────────────────────────────
// KPN Proximity Banner
// ─────────────────────────────────────────────────

/**
 * Proximity check result banner shown in Step 2.
 * Green if no KPN within 3km, red warning if within 3km.
 */
@Composable
fun KPNProximityBanner(
    nearestStoreName: String,
    distanceKm: Double,
    isWithin3km: Boolean,
    modifier: Modifier = Modifier
) {
    val (bg, fg, icon, message) = if (isWithin3km) {
        ProximityStyle(
            bg = KPNColors.AccentRed.copy(alpha = 0.1f),
            fg = KPNColors.AccentRed,
            icon = Icons.Default.Warning,
            message = "⚠ KPN Store within 3km! Nearest: $nearestStoreName (${formatKm(distanceKm)})"
        )
    } else {
        ProximityStyle(
            bg = KPNColors.AccentGreen.copy(alpha = 0.1f),
            fg = KPNColors.AccentGreen,
            icon = Icons.Default.CheckCircle,
            message = "✓ No KPN Store within 3km radius. Nearest: $nearestStoreName (${formatKm(distanceKm)})"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bg, KPNRadius.input)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = fg, modifier = Modifier.size(18.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = fg)
    }
}

private data class ProximityStyle(val bg: Color, val fg: Color, val icon: androidx.compose.ui.graphics.vector.ImageVector, val message: String)
private fun formatKm(km: Double): String {
    return if (km < 1.0) "${(km * 1000).toInt()}m"
    else "${(round(km * 10) / 10.0)}km"
}

// ─────────────────────────────────────────────────
// Step Progress Bar
// ─────────────────────────────────────────────────

/**
 * Thin green progress bar below a wizard step title.
 * Design spec: tracks % of fields completed within the step.
 */
@Composable
fun StepProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(KPNRadius.chip),
        color = KPNColors.AccentGreen,
        trackColor = KPNColors.Border,
        strokeCap = StrokeCap.Round
    )
}

// ─────────────────────────────────────────────────
// Media Upload Slot
// ─────────────────────────────────────────────────

/**
 * Dashed-border upload slot with + icon.
 * Shows thumbnail grid once items are uploaded.
 */
@Composable
fun MediaUploadSlot(
    label: String,
    uploadedCount: Int,
    minRequired: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    thumbnailUrls: List<String> = emptyList()
) {
    val isComplete = uploadedCount >= minRequired
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        KPNSectionHeader(
            title = label,
            trailing = {
                Text(
                    text = "$uploadedCount / $minRequired",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isComplete) KPNColors.AccentGreen else KPNColors.TextSecondary
                )
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .border(
                    width = 1.5.dp,
                    color = if (isComplete) KPNColors.AccentGreen else KPNColors.Border,
                    shape = KPNRadius.uploadSlot
                )
                .background(KPNColors.Background, KPNRadius.uploadSlot)
                .clickable(onClick = onTap),
            contentAlignment = Alignment.Center
        ) {
            if (uploadedCount == 0) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = KPNColors.AccentOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Tap to add",
                        style = MaterialTheme.typography.labelSmall,
                        color = KPNColors.TextSecondary
                    )
                }
            } else {
                // Thumbnail count chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Default.Photo, contentDescription = null, tint = KPNColors.AccentGreen, modifier = Modifier.size(20.dp))
                    Text(
                        text = "$uploadedCount file${if (uploadedCount > 1) "s" else ""} added",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KPNColors.AccentGreen
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add more", tint = KPNColors.AccentOrange, modifier = Modifier.size(20.dp).clickable(onClick = onTap))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Auto-parse indicator chip (GeoIQ fields)
// ─────────────────────────────────────────────────

/**
 * ⚡ Auto-parse indicator — shown on GeoIQ fields that were parsed from PDF.
 */
@Composable
fun AutoParsedChip(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(KPNColors.Primary.copy(alpha = 0.2f), KPNRadius.chip)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = KPNColors.AccentOrange, modifier = Modifier.size(12.dp))
        Text("Auto-parsed", style = MaterialTheme.typography.labelSmall, color = KPNColors.AccentOrange)
    }
}

// ─────────────────────────────────────────────────
// Pre-filled KPN Standard chip
// ─────────────────────────────────────────────────

/**
 * "5 Pre-Filled KPN Std" orange outlined chip shown in Commercials step.
 */
@Composable
fun KPNStdChip(
    count: Int = 5,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, KPNColors.AccentOrange, KPNRadius.chip)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "$count Pre-Filled KPN Std",
            style = MaterialTheme.typography.labelSmall,
            color = KPNColors.AccentOrange
        )
    }
}

// ─────────────────────────────────────────────────
// WhatsApp / Call action button
// ─────────────────────────────────────────────────

/**
 * Green circular WhatsApp icon button shown in Property Detail header.
 */
@Composable
fun WhatsAppButton(
    phone: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(KPNColors.AccentGreen, KPNRadius.chip)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "WhatsApp $phone",
            tint = KPNColors.Surface,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─────────────────────────────────────────────────
// Segmented progress bar (Home screen — store count)
// ─────────────────────────────────────────────────

/**
 * Multi-segment progress bar: green=finalized, orange=pending, blue=revision, gray=sourced.
 */
@Composable
fun StoreProgressBar(
    finalizedCount: Int,
    pendingCount: Int,
    revisionCount: Int,
    sourcedCount: Int,
    totalTarget: Int,
    modifier: Modifier = Modifier
) {
    val total = (finalizedCount + pendingCount + revisionCount + sourcedCount).coerceAtLeast(1)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(KPNRadius.chip)
        ) {
            SegmentBar(fraction = finalizedCount.toFloat() / total, color = KPNColors.AccentGreen)
            SegmentBar(fraction = pendingCount.toFloat() / total, color = KPNColors.AccentOrange)
            SegmentBar(fraction = revisionCount.toFloat() / total, color = KPNColors.StatusSubmitted)
            SegmentBar(fraction = sourcedCount.toFloat() / total, color = KPNColors.StatusDraft)
        }
        // Legend
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LegendDot("Finalized", KPNColors.AccentGreen)
            LegendDot("Pending", KPNColors.AccentOrange)
            LegendDot("Revision", KPNColors.StatusSubmitted)
            LegendDot("Sourced", KPNColors.StatusDraft)
        }
    }
}

@Composable
private fun RowScope.SegmentBar(fraction: Float, color: Color) {
    if (fraction <= 0f) return
    Box(
        modifier = Modifier
            .weight(fraction)
            .fillMaxHeight()
            .background(color)
    )
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Box(Modifier.size(8.dp).background(color, KPNRadius.chip))
        Text(label, style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
    }
}
