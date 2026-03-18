package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.api.PropertyApiService
import com.kpn.falcon.data.api.UpdatePropertyRequest
import com.kpn.falcon.data.models.ApprovalAction
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.data.models.ScoringData
import com.kpn.falcon.data.repository.PropertyRepository
import com.kpn.falcon.domain.usecase.AutoSuggestScoringUseCase
import com.kpn.falcon.domain.usecase.CalculateCompositeScoreUseCase
import com.kpn.falcon.domain.usecase.DeviationCheckUseCase
import com.kpn.falcon.domain.usecase.DeviationResult
import com.kpn.falcon.domain.usecase.SLACountdownUseCase
import com.kpn.falcon.domain.usecase.SuggestedScore
import com.kpn.falcon.util.FilePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PropertyDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val property: PropertyLead? = null,
    val activeTab: Int = 0,
    // GeoIQ upload state
    val geoIqUploading: Boolean = false,
    val geoIqUploadError: String? = null,
    val geoIqUploadSuccess: Boolean = false,
    // SLA
    val slaDeadline: Long = 0L,
    val slaHoursRemaining: Long = 0L,
    val slaIsBreached: Boolean = false,
    // Scoring
    val suggestedScores: List<SuggestedScore> = emptyList(),
    val currentScores: Map<String, Int> = emptyMap(),   // paramKey → score 1-5
    val compositeScore: Float? = null,
    val scoringSaving: Boolean = false,
    val scoringError: String? = null,
    val scoringSaved: Boolean = false,
    // Commercials deviations
    val commercialDeviations: List<DeviationResult> = emptyList(),
    // Approval action
    val approvalSubmitting: Boolean = false,
    val approvalError: String? = null,
    val approvalSuccess: Boolean = false
)

class PropertyDetailViewModel(
    private val propertyId: String,
    private val propertyRepository: PropertyRepository,
    private val propertyApiService: PropertyApiService,
    private val slaCountdown: SLACountdownUseCase,
    private val filePicker: FilePicker,
    private val autoSuggestScoring: AutoSuggestScoringUseCase,
    private val calculateCompositeScore: CalculateCompositeScoreUseCase,
    private val deviationCheck: DeviationCheckUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PropertyDetailUiState())
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    init {
        loadProperty()
        startSlaTimer()
    }

    fun loadProperty() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val property = propertyRepository.getPropertyById(propertyId)
                val deadline = if (property.geoIq?.slaDeadline != null && property.geoIq.slaDeadline > 0L) {
                    property.geoIq.slaDeadline
                } else {
                    slaCountdown.getDeadline(property.createdAt)
                }
                // Auto-suggest scores from store specs
                val suggestions = autoSuggestScoring.execute(
                    area = property.storeSpecs.totalArea,
                    frontage = property.storeSpecs.storeFrontage.toInt(),
                    carParking = property.roadAccess.carParkingCount,
                    bikeParking = property.roadAccess.bikeParkingCount,
                    stepsToEntry = property.storeSpecs.stepsToEntry,
                    roadWidth = property.roadAccess.frontRoadWidth.toInt(),
                    juiceCounterAvailable = property.storeSpecs.juiceCounterAvailable
                )
                // Initialise scores from saved scoring or suggestions
                val existing = property.scoring
                val initScores = mapOf(
                    "area" to (existing?.scoreArea ?: suggestions.find { it.parameter == "area" }?.suggestedScore ?: 0),
                    "frontage" to (existing?.scoreFrontage ?: suggestions.find { it.parameter == "frontage" }?.suggestedScore ?: 0),
                    "carParking" to (existing?.scoreCarParking ?: suggestions.find { it.parameter == "carParking" }?.suggestedScore ?: 0),
                    "bikeParking" to (existing?.scoreBikeParking ?: suggestions.find { it.parameter == "bikeParking" }?.suggestedScore ?: 0),
                    "juiceCounter" to (existing?.scoreJuiceCounter ?: suggestions.find { it.parameter == "juiceCounter" }?.suggestedScore ?: 0),
                    "stepsToEntry" to (existing?.scoreStepsToEntry ?: suggestions.find { it.parameter == "stepsToEntry" }?.suggestedScore ?: 0),
                    "roadWidth" to (existing?.scoreRoadWidth ?: suggestions.find { it.parameter == "roadWidth" }?.suggestedScore ?: 0)
                )
                val composite = computeComposite(initScores)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    property = property,
                    slaDeadline = deadline,
                    slaHoursRemaining = slaCountdown.getHoursRemaining(deadline).coerceAtLeast(0L),
                    slaIsBreached = slaCountdown.isBreached(deadline),
                    suggestedScores = suggestions,
                    currentScores = initScores,
                    compositeScore = composite,
                    commercialDeviations = deviationCheck.check(property.commercials)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load property"
                )
            }
        }
    }

    fun onTabSelected(tab: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    // ─── GeoIQ upload ────────────────────────────────

    fun uploadGeoIQPdf() {
        viewModelScope.launch {
            val picked = filePicker.pickPdf() ?: return@launch
            _uiState.value = _uiState.value.copy(geoIqUploading = true, geoIqUploadError = null)
            try {
                val response = propertyApiService.uploadGeoIQPdf(propertyId, picked.bytes)
                val updatedProperty = _uiState.value.property?.copy(geoIq = response.geoIq)
                _uiState.value = _uiState.value.copy(
                    geoIqUploading = false,
                    geoIqUploadSuccess = true,
                    property = updatedProperty
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    geoIqUploading = false,
                    geoIqUploadError = e.message ?: "Upload failed. Please try again."
                )
            }
        }
    }

    fun dismissGeoIqError() {
        _uiState.value = _uiState.value.copy(geoIqUploadError = null)
    }

    // ─── Scoring ─────────────────────────────────────

    fun updateScore(parameter: String, score: Int) {
        val updated = _uiState.value.currentScores.toMutableMap().apply { put(parameter, score.coerceIn(0, 5)) }
        _uiState.value = _uiState.value.copy(
            currentScores = updated,
            compositeScore = computeComposite(updated),
            scoringSaved = false
        )
    }

    fun saveScoring(salesProjection: Long?, comparableRef: String?, strategicNotes: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(scoringSaving = true, scoringError = null)
            try {
                val s = _uiState.value.currentScores
                val scoring = ScoringData(
                    scoreArea = s["area"],
                    scoreFrontage = s["frontage"],
                    scoreCarParking = s["carParking"],
                    scoreBikeParking = s["bikeParking"],
                    scoreJuiceCounter = s["juiceCounter"],
                    scoreStepsToEntry = s["stepsToEntry"],
                    scoreRoadWidth = s["roadWidth"],
                    compositeScore = _uiState.value.compositeScore,
                    salesProjection = salesProjection,
                    comparableStoreRef = comparableRef,
                    strategicNotes = strategicNotes,
                    confirmedByStateHead = _uiState.value.property?.scoring?.confirmedByStateHead ?: false
                )
                val updated = propertyRepository.updateProperty(
                    propertyId,
                    UpdatePropertyRequest(scoring = scoring)
                )
                _uiState.value = _uiState.value.copy(
                    scoringSaving = false,
                    scoringSaved = true,
                    property = updated
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    scoringSaving = false,
                    scoringError = e.message ?: "Failed to save scoring"
                )
            }
        }
    }

    fun confirmScoringAsStateHead() {
        viewModelScope.launch {
            try {
                val scoring = _uiState.value.property?.scoring?.copy(confirmedByStateHead = true) ?: return@launch
                val updated = propertyRepository.updateProperty(propertyId, UpdatePropertyRequest(scoring = scoring))
                _uiState.value = _uiState.value.copy(property = updated, scoringSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(scoringError = e.message ?: "Confirmation failed")
            }
        }
    }

    // ─── Approval actions ─────────────────────────────

    fun submitApproval(action: ApprovalAction) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(approvalSubmitting = true, approvalError = null)
            try {
                val updated = propertyRepository.submitApproval(propertyId, action)
                _uiState.value = _uiState.value.copy(
                    approvalSubmitting = false,
                    approvalSuccess = true,
                    property = updated,
                    commercialDeviations = deviationCheck.check(updated.commercials)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    approvalSubmitting = false,
                    approvalError = e.message ?: "Action failed. Please try again."
                )
            }
        }
    }

    fun dismissApprovalResult() {
        _uiState.value = _uiState.value.copy(approvalSuccess = false, approvalError = null)
    }

    private fun computeComposite(scores: Map<String, Int>): Float {
        return calculateCompositeScore.execute(
            scoreArea = scores["area"] ?: 0,
            scoreFrontage = scores["frontage"] ?: 0,
            scoreCarParking = scores["carParking"] ?: 0,
            scoreBikeParking = scores["bikeParking"] ?: 0,
            scoreJuiceCounter = scores["juiceCounter"] ?: 0,
            scoreStepsToEntry = scores["stepsToEntry"] ?: 0,
            scoreRoadWidth = scores["roadWidth"] ?: 0
        )
    }

    // ─── SLA live countdown ──────────────────────────

    private fun startSlaTimer() {
        viewModelScope.launch {
            while (isActive) {
                delay(60_000L) // refresh every minute
                val deadline = _uiState.value.slaDeadline
                if (deadline > 0L) {
                    _uiState.value = _uiState.value.copy(
                        slaHoursRemaining = slaCountdown.getHoursRemaining(deadline).coerceAtLeast(0L),
                        slaIsBreached = slaCountdown.isBreached(deadline)
                    )
                }
            }
        }
    }
}
