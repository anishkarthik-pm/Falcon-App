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
import com.kpn.falcon.util.KPNConstants
import com.kpn.falcon.util.Strings

@Composable
fun Step3RoadAccessContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val r = state.roadAccess
    val errors = state.validationErrors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(title = Strings.ROAD_HEADER, fieldProgress = state.stepProgress)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KPNSectionHeader(title = "Road Specs")

            KPNNumberField(
                value = if (r.frontRoadWidth > 0f) r.frontRoadWidth.toString() else "",
                onValueChange = { viewModel.updateRoadAccess(r.copy(frontRoadWidth = it.toFloatOrNull() ?: 0f)) },
                label = Strings.ROAD_FRONT_WIDTH,
                suffix = Strings.FT,
                isRequired = true,
                isError = errors.containsKey("frontRoadWidth"),
                errorMessage = errors["frontRoadWidth"] ?: ""
            )

            KPNSectionHeader(title = Strings.ROAD_TYPE)
            KPNRadioChipGroup(
                options = RoadType.entries,
                selected = r.roadType,
                onSelect = { viewModel.updateRoadAccess(r.copy(roadType = it)) },
                labelFor = { it.displayLabel() }
            )

            KPNSectionHeader(title = Strings.ROAD_CONNECTIVITY)
            KPNMultiChipGroup(
                options = ConnectivityType.entries,
                selected = r.roadConnectivity,
                onToggle = { conn ->
                    val updated = if (conn in r.roadConnectivity) r.roadConnectivity - conn else r.roadConnectivity + conn
                    viewModel.updateRoadAccess(r.copy(roadConnectivity = updated))
                },
                labelFor = { it.displayLabel() }
            )

            HorizontalDivider(color = KPNColors.Border, thickness = 0.5.dp)
            KPNSectionHeader(title = "Parking")

            KPNToggle(
                label = Strings.ROAD_PARKING_AVAILABLE,
                checked = r.parkingAvailable,
                onCheckedChange = { viewModel.updateRoadAccess(r.copy(parkingAvailable = it)) }
            )

            KPNSectionHeader(title = Strings.ROAD_DELIVERY_ACCESS)
            KPNRadioChipGroup(
                options = AccessDifficulty.entries,
                selected = r.deliveryVehicleAccess,
                onSelect = { viewModel.updateRoadAccess(r.copy(deliveryVehicleAccess = it)) },
                labelFor = { it.displayLabel() }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KPNNumberField(
                    value = if (r.carParkingCount > 0) r.carParkingCount.toString() else "",
                    onValueChange = { viewModel.updateRoadAccess(r.copy(carParkingCount = it.toIntOrNull() ?: 0)) },
                    label = Strings.ROAD_CAR_PARKING,
                    isRequired = true,
                    isError = errors.containsKey("carParking"),
                    errorMessage = errors["carParking"] ?: "",
                    inlineHint = Strings.ROAD_MIN_CAR_PARKING_HINT,
                    modifier = Modifier.weight(1f)
                )
                KPNNumberField(
                    value = if (r.bikeParkingCount > 0) r.bikeParkingCount.toString() else "",
                    onValueChange = { viewModel.updateRoadAccess(r.copy(bikeParkingCount = it.toIntOrNull() ?: 0)) },
                    label = Strings.ROAD_BIKE_PARKING,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun RoadType.displayLabel() = when (this) {
    RoadType.MAIN_ROAD -> "Main Road"
    RoadType.RING_ROAD -> "Ring Road"
    RoadType.INTERIOR_ROAD -> "Interior Road"
    RoadType.SERVICE_ROAD -> "Service Road"
}
private fun ConnectivityType.displayLabel() = when (this) {
    ConnectivityType.BUS_ROUTE -> "Bus Route"
    ConnectivityType.METRO_NEARBY -> "Metro Nearby"
    ConnectivityType.HIGHWAY_ACCESS -> "Highway Access"
}
private fun AccessDifficulty.displayLabel() = when (this) {
    AccessDifficulty.EASY -> "Easy"
    AccessDifficulty.MODERATE -> "Moderate"
    AccessDifficulty.DIFFICULT -> "Difficult"
}
