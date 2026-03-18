package com.kpn.falcon.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.kpn.falcon.data.models.*
import com.kpn.falcon.domain.usecase.DeviationResult
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.components.epochMsToDateString
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNElevation
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.viewmodels.PropertyDetailUiState
import com.kpn.falcon.presentation.viewmodels.PropertyDetailViewModel

@Composable
fun CommercialsTab(
    state: PropertyDetailUiState,
    viewModel: PropertyDetailViewModel
) {
    val property = state.property ?: return
    val c = property.commercials
    val deviations = state.commercialDeviations
    var selectedAction by remember { mutableStateOf<ApprovalAction?>(null) }
    var remarks by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // ── Deviation banner ──────────────────────────
        if (deviations.isNotEmpty()) {
            item {
                DeviationSummaryBanner(deviations = deviations)
            }
        }

        // ── Success banner ────────────────────────────
        if (state.approvalSuccess) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(KPNColors.AccentGreen.copy(alpha = 0.10f), KPNRadius.card)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KPNColors.AccentGreen, modifier = Modifier.size(18.dp))
                        Text("Action submitted successfully", style = MaterialTheme.typography.bodyMedium, color = KPNColors.AccentGreen)
                    }
                    IconButton(onClick = viewModel::dismissApprovalResult, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = KPNColors.AccentGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // ── Error banner ──────────────────────────────
        if (state.approvalError != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(KPNColors.AccentRed.copy(alpha = 0.10f), KPNRadius.card)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(state.approvalError, style = MaterialTheme.typography.bodyMedium, color = KPNColors.AccentRed, modifier = Modifier.weight(1f))
                    IconButton(onClick = viewModel::dismissApprovalResult, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = KPNColors.AccentRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // ── Rent metrics card ─────────────────────────
        item {
            RentMetricsCard(c = c)
        }

        // ── Lease terms section ───────────────────────
        item {
            SectionCard(title = "Lease Terms") {
                CommercialField("Landlord Rent", "₹${c.landlordRentPerSqft}/sqft", deviationFor = null)
                if (c.bdOfferedRentPerSqft > 0f) CommercialField("BD Offered Rent", "₹${c.bdOfferedRentPerSqft}/sqft", deviationFor = null)
                CommercialField("Lease Term", "${c.leaseTermYears} years", deviationFor = deviations.find { it.field == "leaseTermYears" })
                CommercialField("Escalation", "${c.escalationPercent}%", deviationFor = deviations.find { it.field == "escalationPercent" })
                CommercialField("Escalation Frequency", "Every ${c.escalationFrequencyYears} years", deviationFor = deviations.find { it.field == "escalationFrequencyYears" })
                if (c.securityDepositMonths > 0) CommercialField("Security Deposit", "${c.securityDepositMonths} months", deviationFor = null)
                if (c.rentFreePeriodDays > 0) CommercialField("Rent Free Period", "${c.rentFreePeriodDays} days", deviationFor = null)
                if (c.lesseeLockInYears > 0) CommercialField("Lessee Lock-in", "${c.lesseeLockInYears} years", deviationFor = null)
                CommercialField("Lessor Lock-in", c.lessorLockIn, deviationFor = deviations.find { it.field == "lessorLockIn" })
                CommercialField("Registration Fees", c.registrationFees.name.replace("_", " ").titleCase(), deviationFor = deviations.find { it.field == "registrationFees" })
                if (c.adjustableAdvance != null) CommercialField("Adjustable Advance", "₹${c.adjustableAdvance}", deviationFor = null)
                if (c.possessionDate != null) CommercialField("Possession Date", epochMsToDateString(c.possessionDate), deviationFor = null)
                if (c.openingMonthAuto != null) CommercialField("Opening Month", epochMsToDateString(c.openingMonthAuto), deviationFor = null)
                if (c.openingQuarter != null) CommercialField("Opening Quarter", c.openingQuarter, deviationFor = null)
            }
        }

        // ── LL Scope section ──────────────────────────
        if (c.llScopeLineItems.isNotEmpty()) {
            item {
                SectionCard(title = "LL Scope of Work") {
                    c.llScopeLineItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.description, style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextPrimary, modifier = Modifier.weight(1f))
                            Text("₹${item.amount}", style = MaterialTheme.typography.bodyMedium, color = KPNColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total LL Scope", style = MaterialTheme.typography.bodyLarge, color = KPNColors.TextPrimary, fontWeight = FontWeight.Bold)
                        Text("₹${c.llScopeTotal}", style = MaterialTheme.typography.bodyLarge, color = KPNColors.TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── Deviation notes ───────────────────────────
        if (!c.deviationFromStdTerms.isNullOrBlank()) {
            item {
                SectionCard(title = "Deviation Notes") {
                    Text(c.deviationFromStdTerms, style = MaterialTheme.typography.bodyMedium, color = KPNColors.AccentOrange)
                }
            }
        }

        // ── Approval actions ──────────────────────────
        item {
            ApprovalActionsCard(
                currentStatus = property.status,
                selectedAction = selectedAction,
                remarks = remarks,
                isSubmitting = state.approvalSubmitting,
                onActionSelect = { selectedAction = if (selectedAction == it) null else it },
                onRemarksChange = { remarks = it },
                onSubmit = {
                    selectedAction?.let { viewModel.submitApproval(it) }
                    selectedAction = null
                    remarks = ""
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────
// Deviation summary banner
// ─────────────────────────────────────────────────

@Composable
private fun DeviationSummaryBanner(deviations: List<DeviationResult>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, KPNColors.AccentOrange, KPNRadius.card),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.AccentOrange.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = KPNColors.AccentOrange, modifier = Modifier.size(18.dp))
                Text(
                    "${deviations.size} deviation${if (deviations.size > 1) "s" else ""} from KPN standard terms",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = KPNColors.AccentOrange
                )
            }
            deviations.forEach { dev ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", color = KPNColors.AccentOrange, style = MaterialTheme.typography.bodyMedium)
                    Column {
                        Text(
                            dev.field.camelCaseToLabel(),
                            style = MaterialTheme.typography.labelMedium,
                            color = KPNColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Actual: ${dev.actualValue}  ·  Standard: ${dev.standardValue}",
                            style = MaterialTheme.typography.labelSmall,
                            color = KPNColors.TextSecondary
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Rent metrics card
// ─────────────────────────────────────────────────

@Composable
private fun RentMetricsCard(c: Commercials) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Rent Metrics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.TextPrimary)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricChip(label = "Rent Ask", value = "₹${c.totalMonthlyRentAsk}", sub = "${formatPct(c.rentAskPercent)}% of rev", modifier = Modifier.weight(1f))
                MetricChip(label = "Offered", value = "₹${c.totalMonthlyRentOffered}", sub = "${formatPct(c.rentOfferedPercent)}% of rev", modifier = Modifier.weight(1f))
                MetricChip(
                    label = "RRR",
                    value = "${formatPct(c.rrr)}%",
                    sub = if (c.rrr > 0f) (if (c.rrr <= 12f) "✓ KPN range" else "⚠ High") else "",
                    highlight = c.rrr > 12f,
                    modifier = Modifier.weight(1f)
                )
            }

            if (c.revenueEstimate > 0L) {
                KPNKeyValueRow("Revenue Estimate", "₹${c.revenueEstimate}/month")
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, sub: String, modifier: Modifier = Modifier, highlight: Boolean = false) {
    val bg = if (highlight) KPNColors.AccentOrange.copy(alpha = 0.08f) else KPNColors.Background
    val vColor = if (highlight) KPNColors.AccentOrange else KPNColors.TextPrimary

    Column(
        modifier = modifier
            .background(bg, KPNRadius.input)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
        Text(value, style = MaterialTheme.typography.titleSmall, color = vColor, fontWeight = FontWeight.Bold)
        if (sub.isNotBlank()) Text(sub, style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
    }
}

// ─────────────────────────────────────────────────
// Commercial field row (with deviation highlight)
// ─────────────────────────────────────────────────

@Composable
private fun CommercialField(label: String, value: String, deviationFor: DeviationResult?) {
    val isDeviation = deviationFor != null
    val borderMod = if (isDeviation) {
        Modifier.border(1.dp, KPNColors.AccentOrange, KPNRadius.input).padding(horizontal = 6.dp, vertical = 2.dp)
    } else {
        Modifier
    }

    Column(modifier = Modifier.fillMaxWidth().then(if (isDeviation) borderMod else Modifier).padding(vertical = 2.dp)) {
        KPNKeyValueRow(
            label = label,
            value = value,
            valueColor = if (isDeviation) KPNColors.AccentOrange else KPNColors.TextPrimary
        )
        if (isDeviation) {
            DeviationFlag(message = "Std: ${deviationFor!!.standardValue}")
            Spacer(Modifier.height(2.dp))
        }
    }
}

// ─────────────────────────────────────────────────
// Approval actions card
// ─────────────────────────────────────────────────

@Composable
private fun ApprovalActionsCard(
    currentStatus: PropertyStatus,
    selectedAction: ApprovalAction?,
    remarks: String,
    isSubmitting: Boolean,
    onActionSelect: (ApprovalAction) -> Unit,
    onRemarksChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    // Actions available depend on current status
    val availableActions = when (currentStatus) {
        PropertyStatus.SUBMITTED -> listOf(ApprovalAction.APPROVE, ApprovalAction.REJECT, ApprovalAction.FORWARD, ApprovalAction.REQUEST_REVISION)
        PropertyStatus.IN_REVIEW -> listOf(ApprovalAction.APPROVE, ApprovalAction.REJECT, ApprovalAction.FORWARD, ApprovalAction.REQUEST_REVISION)
        PropertyStatus.FORWARDED -> listOf(ApprovalAction.APPROVE, ApprovalAction.REJECT, ApprovalAction.REQUEST_REVISION)
        PropertyStatus.DRAFT -> listOf(ApprovalAction.SUBMIT)
        PropertyStatus.REJECTED -> listOf(ApprovalAction.SUBMIT)
        else -> emptyList()
    }

    if (availableActions.isEmpty()) {
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.Surface),
        elevation = CardDefaults.cardElevation(KPNElevation.card)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Approval Actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.TextPrimary)

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableActions.forEach { action ->
                    val isSelected = selectedAction == action
                    val (bg, fg) = actionColors(action, isSelected)

                    Button(
                        onClick = { onActionSelect(action) },
                        modifier = Modifier.weight(1f),
                        shape = KPNRadius.card,
                        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = actionIcon(action),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(action.displayLabel(), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }

            // Remarks input (shown when action is selected)
            if (selectedAction != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    KPNTextField(
                        value = remarks,
                        onValueChange = onRemarksChange,
                        label = "Remarks (optional)",
                        hint = "Add remarks for ${selectedAction.displayLabel()}…",
                        minLines = 2
                    )
                    KPNPrimaryButton(
                        text = "Submit — ${selectedAction.displayLabel()}",
                        onClick = onSubmit,
                        isLoading = isSubmitting,
                        enabled = !isSubmitting
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Section card wrapper
// ─────────────────────────────────────────────────

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
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
            KPNCollapsibleSection(title = title, isExpanded = expanded, onToggle = { expanded = !expanded }) {
                Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    content()
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────

private fun String.titleCase(): String =
    split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }

private fun String.camelCaseToLabel(): String {
    val result = StringBuilder()
    for (ch in this) {
        if (ch.isUpperCase() && result.isNotEmpty()) result.append(' ')
        result.append(if (result.isEmpty()) ch.uppercase() else ch)
    }
    return result.toString()
}

private fun formatPct(value: Float): String {
    val scaled = (value * 10).toInt()
    return "${scaled / 10}.${scaled % 10}"
}

private fun actionColors(action: ApprovalAction, selected: Boolean): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
    val base = when (action) {
        ApprovalAction.APPROVE -> KPNColors.AccentGreen
        ApprovalAction.REJECT -> KPNColors.AccentRed
        ApprovalAction.FORWARD -> KPNColors.AccentOrange
        ApprovalAction.REQUEST_REVISION -> KPNColors.AccentOrange
        ApprovalAction.SUBMIT -> KPNColors.Primary
    }
    return if (selected) Pair(base, KPNColors.Surface) else Pair(base.copy(alpha = 0.12f), base)
}

private fun actionIcon(action: ApprovalAction) = when (action) {
    ApprovalAction.APPROVE -> Icons.Default.CheckCircle
    ApprovalAction.REJECT -> Icons.Default.Cancel
    ApprovalAction.FORWARD -> Icons.Default.Forward
    ApprovalAction.REQUEST_REVISION -> Icons.Default.Edit
    ApprovalAction.SUBMIT -> Icons.Default.Send
}

private fun ApprovalAction.displayLabel() = when (this) {
    ApprovalAction.APPROVE -> "Approve"
    ApprovalAction.REJECT -> "Reject"
    ApprovalAction.FORWARD -> "Forward"
    ApprovalAction.REQUEST_REVISION -> "Revise"
    ApprovalAction.SUBMIT -> "Submit"
}
