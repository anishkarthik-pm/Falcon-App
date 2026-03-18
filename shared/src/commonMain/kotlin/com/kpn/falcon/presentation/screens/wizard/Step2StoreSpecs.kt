package com.kpn.falcon.presentation.screens.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpn.falcon.data.models.*
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.theme.KPNColors
import com.kpn.falcon.presentation.viewmodels.AddPropertyUiState
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.Strings

@Composable
fun Step2StoreSpecsContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val s = state.storeSpecs
    val errors = state.validationErrors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(title = Strings.STORE_SPECS_HEADER, fieldProgress = state.stepProgress)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Site Status
            KPNSectionHeader(title = Strings.STORE_SPECS_SITE_STATUS)
            KPNRadioChipGroup(
                options = SiteStatus.entries,
                selected = s.siteStatus,
                onSelect = { viewModel.updateStoreSpecs(s.copy(siteStatus = it)) },
                labelFor = { it.displayLabel() }
            )

            // Road Facing
            KPNSectionHeader(title = Strings.STORE_SPECS_ROAD_FACING)
            KPNRadioChipGroup(
                options = RoadFacing.entries,
                selected = s.roadFacing,
                onSelect = { viewModel.updateStoreSpecs(s.copy(roadFacing = it)) },
                labelFor = { if (it == RoadFacing.MAIN) "Main" else "Side" }
            )

            // Floors
            KPNSectionHeader(title = Strings.STORE_SPECS_FLOORS)
            KPNMultiChipGroup(
                options = FloorType.entries,
                selected = s.floors,
                onToggle = { floor ->
                    val updated = if (floor in s.floors) s.floors - floor else s.floors + floor
                    viewModel.updateStoreSpecs(s.copy(floors = updated))
                },
                labelFor = { it.displayLabel() }
            )

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // Area fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = if (s.totalArea > 0) s.totalArea.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(totalArea = it.toIntOrNull() ?: 0)) },
                    label = Strings.STORE_SPECS_TOTAL_AREA,
                    suffix = Strings.SQFT,
                    isRequired = true,
                    isError = errors.containsKey("totalArea"),
                    errorMessage = errors["totalArea"] ?: "",
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (s.carpetArea > 0) s.carpetArea.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(carpetArea = it.toIntOrNull() ?: 0)) },
                    label = Strings.STORE_SPECS_CARPET_AREA,
                    suffix = Strings.SQFT,
                    isRequired = true,
                    isDeviation = viewModel.isCarpetAreaDeviation(),
                    modifier = Modifier.weight(1f)
                )
            }
            if (viewModel.isCarpetAreaDeviation()) {
                DeviationFlag(message = Strings.DEVIATION_FROM_BASELINE)
            }

            // Steps + Ceiling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = if (s.stepsToEntry > 0) s.stepsToEntry.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(stepsToEntry = it.toIntOrNull() ?: 0)) },
                    label = Strings.STORE_SPECS_STEPS,
                    isRequired = true,
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (s.ceilingHeight > 0f) s.ceilingHeight.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(ceilingHeight = it.toFloatOrNull() ?: 0f)) },
                    label = Strings.STORE_SPECS_CEILING_HEIGHT,
                    suffix = Strings.FT,
                    modifier = Modifier.weight(1f)
                )
            }

            // Frontage + Facade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = if (s.storeFrontage > 0f) s.storeFrontage.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(storeFrontage = it.toFloatOrNull() ?: 0f)) },
                    label = Strings.STORE_SPECS_STORE_FRONTAGE,
                    suffix = Strings.FT,
                    isRequired = true,
                    isError = errors.containsKey("storeFrontage"),
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (s.facadeFrontage > 0f) s.facadeFrontage.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(facadeFrontage = it.toFloatOrNull() ?: 0f)) },
                    label = Strings.STORE_SPECS_FACADE_FRONTAGE,
                    suffix = Strings.FT,
                    modifier = Modifier.weight(1f)
                )
            }

            KPNTextField(
                value = s.storeDimensions,
                onValueChange = { viewModel.updateStoreSpecs(s.copy(storeDimensions = it)) },
                label = Strings.STORE_SPECS_DIMENSIONS,
                hint = "e.g. 45ft × 80ft"
            )

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // Infrastructure
            KPNSectionHeader(title = Strings.STORE_SPECS_INFRASTRUCTURE)
            KPNMultiChipGroup(
                options = InfraType.entries,
                selected = s.infrastructure,
                onToggle = { infra ->
                    val updated = if (infra in s.infrastructure) s.infrastructure - infra else s.infrastructure + infra
                    viewModel.updateStoreSpecs(s.copy(infrastructure = updated))
                },
                labelFor = { it.displayLabel() }
            )

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // Signage
            KPNSectionHeader(title = Strings.STORE_SPECS_SIGNAGE)
            KPNRadioChipGroup(
                options = SignageType.entries,
                selected = s.signageAvailability,
                onSelect = { viewModel.updateStoreSpecs(s.copy(signageAvailability = it)) },
                labelFor = { it.displayLabel() }
            )

            if (s.signageAvailability != SignageType.NOT_AVAILABLE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KPNNumberField(
                        value = s.signageWidth?.toString() ?: "",
                        onValueChange = { viewModel.updateStoreSpecs(s.copy(signageWidth = it.toFloatOrNull())) },
                        label = Strings.STORE_SPECS_SIGNAGE_WIDTH,
                        suffix = Strings.FT,
                        modifier = Modifier.weight(1f)
                    )
                    KPNNumberField(
                        value = s.additionalSignageRequired?.toString() ?: "",
                        onValueChange = { viewModel.updateStoreSpecs(s.copy(additionalSignageRequired = it.toIntOrNull())) },
                        label = Strings.STORE_SPECS_ADD_SIGNAGE,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)

            // Juice counter
            KPNToggle(
                label = Strings.STORE_SPECS_JUICE_COUNTER,
                checked = s.juiceCounterAvailable,
                onCheckedChange = { viewModel.updateStoreSpecs(s.copy(juiceCounterAvailable = it)) }
            )
            if (s.juiceCounterAvailable) {
                KPNNumberField(
                    value = s.juiceCounterArea?.toString() ?: "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(juiceCounterArea = it.toFloatOrNull())) },
                    label = Strings.STORE_SPECS_JUICE_AREA,
                    suffix = Strings.SQFT
                )
            }

            // Power load + Road width
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = s.powerLoad?.toString() ?: "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(powerLoad = it.toFloatOrNull())) },
                    label = Strings.STORE_SPECS_POWER_LOAD,
                    suffix = Strings.KVA,
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (s.roadWidth > 0f) s.roadWidth.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(roadWidth = it.toFloatOrNull() ?: 0f)) },
                    label = "Road Width",
                    suffix = Strings.FT,
                    isRequired = true,
                    isError = errors.containsKey("roadWidth"),
                    modifier = Modifier.weight(1f)
                )
            }

            // Parking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = if (s.carParking > 0) s.carParking.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(carParking = it.toIntOrNull() ?: 0)) },
                    label = "Car Parking",
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (s.bikeParking > 0) s.bikeParking.toString() else "",
                    onValueChange = { viewModel.updateStoreSpecs(s.copy(bikeParking = it.toIntOrNull() ?: 0)) },
                    label = "Bike Parking",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// Display label helpers
private fun SiteStatus.displayLabel() = when (this) {
    SiteStatus.READY_TO_MOVE -> "Ready To Move In"
    SiteStatus.BTS -> "BTS"
    SiteStatus.UNDER_CONSTRUCTION -> "Under Construction BTS"
    SiteStatus.FITOUT -> "Fitout"
}
private fun FloorType.displayLabel() = when (this) {
    FloorType.GROUND -> "Ground Floor"
    FloorType.STILT -> "Stilt"
    FloorType.FIRST -> "1st Floor"
    FloorType.SECOND -> "2nd Floor"
    FloorType.UNDERGROUND -> "Under Ground"
}
private fun InfraType.displayLabel() = when (this) {
    InfraType.AC -> "AC"
    InfraType.ELECTRICAL_WIRING -> "Electrical Wiring"
    InfraType.CCTV -> "CCTV"
    InfraType.PLUMBING -> "Plumbing"
    InfraType.FIRE_SAFETY -> "Fire Safety"
    InfraType.FLOORING -> "Flooring"
    InfraType.TOILET -> "Toilet"
}
private fun SignageType.displayLabel() = when (this) {
    SignageType.AVAILABLE -> "Available"
    SignageType.PARTIALLY_AVAILABLE -> "Partially Available"
    SignageType.NOT_AVAILABLE -> "Not Available"
}
