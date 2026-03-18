package com.kpn.falcon.presentation.screens.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kpn.falcon.data.models.*
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.viewmodels.AddPropertyUiState
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.KPNConstants
import com.kpn.falcon.util.Strings

@Composable
fun Step5CommercialsContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val c = state.commercials
    val metrics = state.rentMetrics
    val deviations = state.deviations
    val errors = state.validationErrors

    fun isDeviation(field: String) = deviations.any { it.field == field }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(title = Strings.COMMERCIALS_HEADER, fieldProgress = state.stepProgress)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Rent Details ──────────────────────────────
            KPNSectionHeader(title = "Rent Details")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = if (c.landlordRentPerSqft > 0f) c.landlordRentPerSqft.toString() else "",
                    onValueChange = { viewModel.updateCommercials(c.copy(landlordRentPerSqft = it.toFloatOrNull() ?: 0f)) },
                    label = Strings.COMMERCIALS_LANDLORD_RENT,
                    suffix = "₹/sqft",
                    isRequired = true,
                    isError = errors.containsKey("landlordRent"),
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (c.bdOfferedRentPerSqft > 0f) c.bdOfferedRentPerSqft.toString() else "",
                    onValueChange = { viewModel.updateCommercials(c.copy(bdOfferedRentPerSqft = it.toFloatOrNull() ?: 0f)) },
                    label = Strings.COMMERCIALS_BD_RENT,
                    suffix = "₹/sqft",
                    modifier = Modifier.weight(1f)
                )
            }

            // Auto-calculated rent totals
            if (metrics != null) {
                RentCalculatedRow(
                    monthlyRentAsk = metrics.totalMonthlyRentAsk,
                    monthlyRentOffered = metrics.totalMonthlyRentOffered
                )
            }

            // ── Revenue & RRR ─────────────────────────────
            KPNSectionHeader(title = "Revenue & RRR")

            KPNNumberField(
                value = if (c.revenueEstimate > 0L) c.revenueEstimate.toString() else "",
                onValueChange = { viewModel.updateCommercials(c.copy(revenueEstimate = it.toLongOrNull() ?: 0L)) },
                label = Strings.COMMERCIALS_REVENUE_ESTIMATE,
                suffix = "₹/month",
                isRequired = true,
                isError = errors.containsKey("revenueEstimate")
            )

            if (metrics != null) {
                RRRCalculatedRow(
                    rentAskPercent = metrics.rentAskPercent,
                    rentOfferedPercent = metrics.rentOfferedPercent
                )
            }

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // ── Lease Terms ───────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KPNSectionHeader(title = "Lease Terms", modifier = Modifier.weight(1f))
                KPNStdChip(count = 5)
            }

            KPNNumberField(
                value = c.securityDepositMonths.let { if (it > 0) it.toString() else "" },
                onValueChange = { viewModel.updateCommercials(c.copy(securityDepositMonths = it.toIntOrNull() ?: 0)) },
                label = Strings.COMMERCIALS_SECURITY_DEPOSIT,
                suffix = Strings.MONTH
            )

            KPNNumberField(
                value = c.leaseTermYears.toString(),
                onValueChange = {
                    viewModel.updateCommercials(
                        c.copy(leaseTermYears = it.toIntOrNull() ?: KPNConstants.DEFAULT_LEASE_TERM_YEARS)
                    )
                },
                label = Strings.COMMERCIALS_LEASE_TERM,
                suffix = Strings.YEARS,
                isDeviation = isDeviation("leaseTermYears")
            )
            if (isDeviation("leaseTermYears")) DeviationFlag()

            KPNTextField(
                value = "${c.escalationPercent.toInt()}% per ${c.escalationFrequencyYears}year",
                onValueChange = { /* parsed separately */ },
                label = Strings.COMMERCIALS_ESCALATION,
                isDeviation = isDeviation("escalationPercent") || isDeviation("escalationFrequencyYears"),
                readOnly = false
            )
            if (isDeviation("escalationPercent")) DeviationFlag()

            KPNTextField(
                value = if (c.rentFreePeriodDays > 0) "${c.rentFreePeriodDays} days" else "",
                onValueChange = {
                    val days = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                    viewModel.updateCommercials(c.copy(rentFreePeriodDays = days))
                },
                label = Strings.COMMERCIALS_RENT_FREE,
                hint = "e.g. 30 days"
            )

            KPNTextField(
                value = if (c.lesseeLockInYears > 0) "${c.lesseeLockInYears} Year" else "",
                onValueChange = {
                    val yrs = it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
                    viewModel.updateCommercials(c.copy(lesseeLockInYears = yrs))
                },
                label = Strings.COMMERCIALS_LESSEE_LOCK_IN,
                hint = "e.g. 1 Year"
            )

            KPNTextField(
                value = c.lessorLockIn,
                onValueChange = { viewModel.updateCommercials(c.copy(lessorLockIn = it)) },
                label = Strings.COMMERCIALS_LESSOR_LOCK_IN,
                isDeviation = isDeviation("lessorLockIn")
            )
            if (isDeviation("lessorLockIn")) DeviationFlag()

            KPNDatePickerField(
                label = Strings.COMMERCIALS_POSSESSION_DATE,
                selectedDateMs = c.possessionDate,
                onDateSelected = { viewModel.updateCommercials(c.copy(possessionDate = it)) }
            )

            // Auto-calculated opening month (read-only, deviation border)
            KPNTextField(
                value = c.openingMonthAuto?.let { epochMsToDateString(it) } ?: "",
                onValueChange = {},
                label = Strings.COMMERCIALS_OPENING_MONTH,
                readOnly = true,
                isDeviation = c.openingMonthAuto != null
            )
            if (c.openingMonthAuto != null) DeviationFlag(message = "Auto-calculated")

            KPNTextField(
                value = c.openingQuarter ?: "",
                onValueChange = {},
                label = Strings.COMMERCIALS_OPENING_QUARTER,
                readOnly = true,
                isDeviation = c.openingQuarter != null
            )

            KPNSectionHeader(title = Strings.COMMERCIALS_REGISTRATION_FEES)
            KPNRadioChipGroup(
                options = RegistrationFees.entries,
                selected = c.registrationFees,
                onSelect = { viewModel.updateCommercials(c.copy(registrationFees = it)) },
                labelFor = { it.displayLabel() }
            )
            if (isDeviation("registrationFees")) DeviationFlag()

            KPNTextField(
                value = c.deviationFromStdTerms ?: "",
                onValueChange = { viewModel.updateCommercials(c.copy(deviationFromStdTerms = it.ifBlank { null })) },
                label = Strings.COMMERCIALS_DEVIATION,
                hint = "Describe any deviations from standard terms...",
                singleLine = false,
                maxLines = 3
            )

            KPNNumberField(
                value = c.adjustableAdvance?.toString() ?: "",
                onValueChange = { viewModel.updateCommercials(c.copy(adjustableAdvance = it.toLongOrNull())) },
                label = Strings.COMMERCIALS_ADJUSTABLE_ADVANCE,
                suffix = "₹"
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Auto-calculated display rows ──────────────────

@Composable
private fun RentCalculatedRow(monthlyRentAsk: Long, monthlyRentOffered: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KPNColors.AccentGreen.copy(alpha = 0.06f), KPNRadius.input)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("₹${formatLakhs(monthlyRentAsk)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.TextPrimary)
            Text("Total Monthly Rent", style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹${formatLakhs(monthlyRentOffered)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.AccentGreen)
            Text("ABS. Offered", style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
        }
    }
}

@Composable
private fun RRRCalculatedRow(rentAskPercent: Float, rentOfferedPercent: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KPNColors.AccentGreen.copy(alpha = 0.06f), KPNRadius.input)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text("${"%.2f".format(rentAskPercent)}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.TextPrimary)
            Text("Rent Ask%", style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
        }
        Column {
            Text("${"%.2f".format(rentOfferedPercent)}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = KPNColors.AccentGreen)
            Text("Rent Offered%", style = MaterialTheme.typography.labelSmall, color = KPNColors.TextSecondary)
        }
    }
}

private fun formatLakhs(amount: Long): String {
    return when {
        amount >= 10_00_000L -> "${"%.2f".format(amount / 1_00_000.0)}L"
        amount >= 1_000L -> "${"%.1f".format(amount / 1000.0)}K"
        else -> amount.toString()
    }
}

private fun RegistrationFees.displayLabel() = when (this) {
    RegistrationFees.EQUALLY_SHARED -> "Equally Shared"
    RegistrationFees.LANDLORD_BEARS -> "Landlord Bears"
    RegistrationFees.KPN_BEARS -> "KPN Bears"
}
