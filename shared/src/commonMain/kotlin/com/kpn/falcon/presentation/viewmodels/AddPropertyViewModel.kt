package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.api.CreatePropertyRequest
import com.kpn.falcon.data.models.*
import com.kpn.falcon.data.repository.PropertyRepository
import com.kpn.falcon.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddPropertyUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 7,
    val propertyId: String = "Pending",
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    val location: LocationData = LocationData(),
    val storeSpecs: StoreSpecs = StoreSpecs(),
    val roadAccess: RoadAccess = RoadAccess(),
    val media: MediaData = MediaData(),
    val commercials: Commercials = Commercials(),
    val competitors: List<Competitor> = emptyList(),
    val contact: ContactInfo = ContactInfo(),
    val rentMetrics: RentMetrics? = null,
    val deviations: List<DeviationResult> = emptyList(),
    val gpsCapturing: Boolean = false
)

class AddPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val calculateRentMetrics: CalculateRentMetricsUseCase,
    private val deviationCheck: DeviationCheckUseCase,
    private val proximityCheck: KPNProximityCheckUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPropertyUiState())
    val uiState: StateFlow<AddPropertyUiState> = _uiState.asStateFlow()

    fun goToStep(step: Int) {
        _uiState.value = _uiState.value.copy(currentStep = step.coerceIn(1, 7))
    }

    fun nextStep() = goToStep(_uiState.value.currentStep + 1)
    fun previousStep() = goToStep(_uiState.value.currentStep - 1)

    fun updateLocation(location: LocationData) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateStoreSpecs(specs: StoreSpecs) {
        _uiState.value = _uiState.value.copy(storeSpecs = specs)
    }

    fun updateRoadAccess(roadAccess: RoadAccess) {
        _uiState.value = _uiState.value.copy(roadAccess = roadAccess)
    }

    fun updateMedia(media: MediaData) {
        _uiState.value = _uiState.value.copy(media = media)
    }

    fun updateCommercials(commercials: Commercials) {
        val metrics = if (commercials.revenueEstimate > 0) {
            calculateRentMetrics.execute(
                carpetArea = _uiState.value.storeSpecs.carpetArea,
                landlordRent = commercials.landlordRentPerSqft,
                offeredRent = commercials.bdOfferedRentPerSqft,
                revenueEstimate = commercials.revenueEstimate
            )
        } else null

        val deviations = deviationCheck.check(commercials)

        _uiState.value = _uiState.value.copy(
            commercials = commercials,
            rentMetrics = metrics,
            deviations = deviations
        )
    }

    fun updateCompetitors(competitors: List<Competitor>) {
        _uiState.value = _uiState.value.copy(competitors = competitors)
    }

    fun updateContact(contact: ContactInfo) {
        _uiState.value = _uiState.value.copy(contact = contact)
    }

    fun submitProperty(bdExecutiveId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val state = _uiState.value
                val created = propertyRepository.createProperty(
                    CreatePropertyRequest(bdExecutiveId = bdExecutiveId, location = state.location)
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSubmitted = true,
                    propertyId = created.propertyId
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Submission failed"
                )
            }
        }
    }
}
