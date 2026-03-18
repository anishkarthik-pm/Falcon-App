package com.kpn.falcon.presentation.screens.wizard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kpn.falcon.data.models.LocationData
import com.kpn.falcon.presentation.components.*
import com.kpn.falcon.presentation.viewmodels.AddPropertyUiState
import com.kpn.falcon.presentation.viewmodels.AddPropertyViewModel
import com.kpn.falcon.util.Strings
import com.kpn.falcon.util.GpsProvider
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Step1LocationContent(
    viewModel: AddPropertyViewModel,
    state: AddPropertyUiState
) {
    val scope = rememberCoroutineScope()
    val gpsProvider = koinInject<GpsProvider>()
    val loc = state.location
    val errors = state.validationErrors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WizardStepHeader(
            title = Strings.LOCATION_HEADER,
            fieldProgress = state.stepProgress
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Read-only property lead details block
            PropertyLeadDetailsBlock(
                propertyId = state.propertyId,
                bdExecutiveName = "—",
                dateAdded = epochMsToDateString(currentEpochMs())
            )

            KPNSectionHeader(title = "Address Details")

            // GPS capture
            GPSCaptureField(
                lat = loc.gpsLat,
                lng = loc.gpsLng,
                isCapturing = state.gpsCapturing,
                onCapture = {
                    scope.launch {
                        viewModel.setGpsCapturing(true)
                        val coords = gpsProvider.getCurrentLocation()
                        if (coords != null) {
                            viewModel.onGpsCaptured(coords.lat, coords.lng)
                            // Auto-fill city/state/pincode from reverse geocode in Task 12
                        } else {
                            viewModel.onGpsError("Could not get location. Please check permissions.")
                        }
                    }
                }
            )

            if (state.gpsError != null) {
                DeviationFlag(message = state.gpsError!!)
            }

            // KPN Proximity banner (shown once GPS is captured)
            if (loc.gpsLat != null && loc.gpsLng != null) {
                loc.kpnProximityCheck?.let { result ->
                    KPNProximityBanner(
                        nearestStoreName = result.nearestStoreName,
                        distanceKm = result.distanceKm,
                        isWithin3km = result.isWithin3km
                    )
                }
            }

            // Auto-filled from GPS reverse geocode (read-only when filled)
            KPNTextField(
                value = loc.city,
                onValueChange = { viewModel.updateLocation(loc.copy(city = it)) },
                label = Strings.LOCATION_CITY,
                isRequired = true,
                isError = errors.containsKey("city"),
                errorMessage = errors["city"] ?: "",
                readOnly = loc.gpsLat != null && loc.city.isNotBlank()
            )

            KPNTextField(
                value = loc.state,
                onValueChange = { viewModel.updateLocation(loc.copy(state = it)) },
                label = Strings.LOCATION_STATE,
                isRequired = true,
                isError = errors.containsKey("state"),
                errorMessage = errors["state"] ?: "",
                readOnly = loc.gpsLat != null && loc.state.isNotBlank()
            )

            KPNTextField(
                value = loc.pincode,
                onValueChange = { viewModel.updateLocation(loc.copy(pincode = it)) },
                label = Strings.LOCATION_PINCODE,
                isRequired = true,
                isError = errors.containsKey("pincode"),
                errorMessage = errors["pincode"] ?: "",
                keyboardType = KeyboardType.Number,
                readOnly = loc.gpsLat != null && loc.pincode.isNotBlank()
            )

            KPNTextField(
                value = loc.fullAddress,
                onValueChange = { viewModel.updateLocation(loc.copy(fullAddress = it)) },
                label = Strings.LOCATION_FULL_ADDRESS,
                isRequired = true,
                isError = errors.containsKey("fullAddress"),
                errorMessage = errors["fullAddress"] ?: "",
                singleLine = false,
                maxLines = 4,
                hint = "Building name, street, area..."
            )

            KPNTextField(
                value = loc.nearestLandmark ?: "",
                onValueChange = { viewModel.updateLocation(loc.copy(nearestLandmark = it.ifBlank { null })) },
                label = Strings.LOCATION_NEAREST_LANDMARK,
                hint = "Nearest landmark"
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun currentEpochMs(): Long {
    return com.kpn.falcon.domain.usecase.currentTimeMillis()
}
