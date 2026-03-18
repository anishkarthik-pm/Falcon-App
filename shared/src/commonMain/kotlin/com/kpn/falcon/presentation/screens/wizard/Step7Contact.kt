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
import com.kpn.falcon.data.models.ContactInfo
import com.kpn.falcon.data.models.ContactSource
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.theme.KPNRadius
import com.kpn.falcon.presentation.viewmodels.AddPropertyUiState
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.Strings

@Composable
fun Step7ContactContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val c = state.contact
    val errors = state.validationErrors
    val showBrokerFields = c.source == ContactSource.BROKER

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(title = Strings.CONTACT_HEADER, fieldProgress = state.stepProgress)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Source ────────────────────────────────────
            KPNSectionHeader(title = Strings.CONTACT_SOURCE_SECTION)
            KPNRadioChipGroup(
                options = ContactSource.entries,
                selected = c.source,
                onSelect = { viewModel.updateContact(c.copy(source = it)) },
                labelFor = { it.displayLabel() }
            )

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // ── Landlord details ──────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KPNSectionHeader(title = Strings.CONTACT_LANDLORD_SECTION, modifier = Modifier.weight(1f))
                KPNStdChip(count = 2)
            }

            KPNTextField(
                value = c.landlordName,
                onValueChange = { viewModel.updateContact(c.copy(landlordName = it)) },
                label = Strings.CONTACT_LANDLORD_NAME,
                isRequired = true,
                isError = errors.containsKey("landlordName"),
                hint = "Full name"
            )
            if (errors.containsKey("landlordName")) {
                Text(
                    errors["landlordName"]!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.AccentRed
                )
            }

            KPNTextField(
                value = c.landlordPhone,
                onValueChange = {
                    if (it.length <= 10) viewModel.updateContact(c.copy(landlordPhone = it.filter { ch -> ch.isDigit() }))
                },
                label = Strings.CONTACT_LANDLORD_PHONE,
                isRequired = true,
                isError = errors.containsKey("landlordPhone"),
                hint = "10-digit mobile number"
            )
            if (errors.containsKey("landlordPhone")) {
                Text(
                    errors["landlordPhone"]!!,
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.AccentRed
                )
            }

            KPNTextField(
                value = c.landlordEmail ?: "",
                onValueChange = { viewModel.updateContact(c.copy(landlordEmail = it.ifBlank { null })) },
                label = Strings.CONTACT_LANDLORD_EMAIL,
                hint = "Optional"
            )

            // ── WhatsApp quick-contact ────────────────────
            if (c.landlordPhone.length == 10) {
                WhatsAppButton(
                    phone = c.landlordPhone,
                    message = "Hello, I am from KPN Farm Fresh. I am interested in your property.",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Broker details (visible when source = BROKER) ──
            if (showBrokerFields) {
                HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

                KPNSectionHeader(title = Strings.CONTACT_BROKER_SECTION)

                KPNTextField(
                    value = c.brokerName ?: "",
                    onValueChange = { viewModel.updateContact(c.copy(brokerName = it.ifBlank { null })) },
                    label = Strings.CONTACT_BROKER_NAME,
                    hint = "Broker / Agent name"
                )

                KPNTextField(
                    value = c.brokerPhone ?: "",
                    onValueChange = {
                        val filtered = it.filter { ch -> ch.isDigit() }.take(10)
                        viewModel.updateContact(c.copy(brokerPhone = filtered.ifBlank { null }))
                    },
                    label = Strings.CONTACT_BROKER_PHONE,
                    hint = "10-digit mobile number"
                )

                if ((c.brokerPhone?.length ?: 0) == 10) {
                    WhatsAppButton(
                        phone = c.brokerPhone!!,
                        message = "Hello, I am from KPN Farm Fresh regarding the property you listed.",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // ── Submission summary card ───────────────────
            SubmissionSummaryCard(state = state)

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SubmissionSummaryCard(state: AddPropertyUiState) {
    val loc = state.location
    val specs = state.storeSpecs

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = KPNRadius.card,
        colors = CardDefaults.cardColors(containerColor = KPNColors.AccentGreen.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = Strings.CONTACT_SUMMARY_TITLE,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = KPNColors.TextPrimary
            )

            SummaryRow(label = "Location", value = loc.city.ifBlank { "—" })
            SummaryRow(label = "Area", value = if (specs.totalArea > 0) "${specs.totalArea} sqft" else "—")
            SummaryRow(
                label = "Rent Ask",
                value = if (state.commercials.landlordRentPerSqft > 0f) "₹${state.commercials.landlordRentPerSqft}/sqft" else "—"
            )
            SummaryRow(label = "Competitors", value = "${state.competitors.size} mapped")
            SummaryRow(
                label = "Media",
                value = buildString {
                    val m = state.media
                    val total = m.exteriorPhotos.size + m.internalPhotos.size + m.competitorPhotos.size
                    append("$total photos, ${m.videos.size} video(s)")
                }
            )
            SummaryRow(label = "Deviations", value = if (state.deviations.isEmpty()) "None" else "${state.deviations.size} flagged")

            if (state.deviations.isNotEmpty()) {
                Text(
                    text = Strings.CONTACT_DEVIATION_WARNING,
                    style = MaterialTheme.typography.labelSmall,
                    color = KPNColors.AccentOrange,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = KPNColors.TextSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, color = KPNColors.TextPrimary, fontWeight = FontWeight.Medium)
    }
}

private fun ContactSource.displayLabel() = when (this) {
    ContactSource.BROKER -> "Broker"
    ContactSource.DIRECT -> "Direct"
    ContactSource.SELF_SOURCED -> "Self Sourced"
}
