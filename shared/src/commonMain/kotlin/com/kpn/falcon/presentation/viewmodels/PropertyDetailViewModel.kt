package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.api.PropertyApiService
import com.kpn.falcon.data.models.GeoIQData
import com.kpn.falcon.data.models.PropertyLead
import com.kpn.falcon.data.repository.PropertyRepository
import com.kpn.falcon.domain.usecase.SLACountdownUseCase
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
    val slaIsBreached: Boolean = false
)

class PropertyDetailViewModel(
    private val propertyId: String,
    private val propertyRepository: PropertyRepository,
    private val propertyApiService: PropertyApiService,
    private val slaCountdown: SLACountdownUseCase,
    private val filePicker: FilePicker
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
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    property = property,
                    slaDeadline = deadline,
                    slaHoursRemaining = slaCountdown.getHoursRemaining(deadline).coerceAtLeast(0L),
                    slaIsBreached = slaCountdown.isBreached(deadline)
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
