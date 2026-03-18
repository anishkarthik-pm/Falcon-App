package com.kpn.falcon.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kpn.falcon.data.api.CreatePropertyRequest
import com.kpn.falcon.data.api.UpdatePropertyRequest
import com.kpn.falcon.data.models.*
import com.kpn.falcon.data.repository.PropertyRepository
import com.kpn.falcon.domain.usecase.*
import com.kpn.falcon.util.KPNConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddPropertyUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 7,
    // Server-assigned ID once first POST succeeds
    val propertyId: String = "Pending",
    val savedPropertyId: String? = null,
    // Loading / submission
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: String? = null,
    // Step data
    val location: LocationData = LocationData(),
    val storeSpecs: StoreSpecs = StoreSpecs(),
    val roadAccess: RoadAccess = RoadAccess(),
    val media: MediaData = MediaData(),
    val commercials: Commercials = Commercials(
        leaseTermYears = KPNConstants.DEFAULT_LEASE_TERM_YEARS,
        escalationPercent = KPNConstants.DEFAULT_ESCALATION_PERCENT,
        escalationFrequencyYears = KPNConstants.DEFAULT_ESCALATION_FREQUENCY_YEARS,
        lessorLockIn = KPNConstants.DEFAULT_LESSOR_LOCK_IN,
        registrationFees = KPNConstants.DEFAULT_REGISTRATION_FEES
    ),
    val competitors: List<Competitor> = emptyList(),
    val contact: ContactInfo = ContactInfo(),
    // Derived / calculated
    val rentMetrics: RentMetrics? = null,
    val deviations: List<DeviationResult> = emptyList(),
    // GPS state
    val gpsCapturing: Boolean = false,
    val gpsError: String? = null,
    // Per-step validation errors (field name → message)
    val validationErrors: Map<String, String> = emptyMap(),
    // Step completion progress (0f–1f)
    val stepProgress: Float = 0f
)

class AddPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val calculateRentMetrics: CalculateRentMetricsUseCase,
    private val deviationCheck: DeviationCheckUseCase,
    private val proximityCheck: KPNProximityCheckUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPropertyUiState())
    val uiState: StateFlow<AddPropertyUiState> = _uiState.asStateFlow()

    // ─── Step navigation ─────────────────────────────

    fun goToStep(step: Int) {
        val clamped = step.coerceIn(1, 7)
        _uiState.value = _uiState.value.copy(
            currentStep = clamped,
            validationErrors = emptyMap(),
            stepProgress = computeProgress(clamped)
        )
    }

    fun nextStep(): Boolean {
        val errors = validateCurrentStep()
        if (errors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(validationErrors = errors)
            return false
        }
        val next = (_uiState.value.currentStep + 1).coerceAtMost(7)
        goToStep(next)
        return true
    }

    fun previousStep() {
        goToStep(_uiState.value.currentStep - 1)
    }

    // ─── Step 1: Location ────────────────────────────

    fun setGpsCapturing(capturing: Boolean) {
        _uiState.value = _uiState.value.copy(gpsCapturing = capturing, gpsError = null)
    }

    fun onGpsCaptured(lat: Double, lng: Double) {
        val loc = _uiState.value.location.copy(gpsLat = lat, gpsLng = lng)
        _uiState.value = _uiState.value.copy(
            location = loc,
            gpsCapturing = false,
            gpsError = null,
            stepProgress = computeProgress(1)
        )
    }

    fun onGpsError(message: String) {
        _uiState.value = _uiState.value.copy(gpsCapturing = false, gpsError = message)
    }

    fun updateLocation(location: LocationData) {
        _uiState.value = _uiState.value.copy(
            location = location,
            stepProgress = computeProgress(1)
        )
    }

    // ─── Step 2: Store Specs ─────────────────────────

    fun updateStoreSpecs(specs: StoreSpecs) {
        _uiState.value = _uiState.value.copy(
            storeSpecs = specs,
            stepProgress = computeProgress(2)
        )
    }

    fun isCarpetAreaDeviation(): Boolean {
        val s = _uiState.value.storeSpecs
        if (s.totalArea == 0) return false
        return s.carpetArea.toFloat() / s.totalArea < KPNConstants.CARPET_AREA_EFFICIENCY_THRESHOLD
    }

    // ─── Step 3: Road Access ─────────────────────────

    fun updateRoadAccess(roadAccess: RoadAccess) {
        _uiState.value = _uiState.value.copy(
            roadAccess = roadAccess,
            stepProgress = computeProgress(3)
        )
    }

    // ─── Step 4: Media ───────────────────────────────

    fun addExteriorPhoto(url: String) = updateMedia {
        it.copy(exteriorPhotos = it.exteriorPhotos + url)
    }
    fun addInternalPhoto(url: String) = updateMedia {
        it.copy(internalPhotos = it.internalPhotos + url)
    }
    fun addCompetitorPhoto(url: String) = updateMedia {
        it.copy(competitorPhotos = it.competitorPhotos + url)
    }
    fun addVideo(url: String) = updateMedia { it.copy(videos = it.videos + url) }
    fun addDocument(url: String) = updateMedia { it.copy(documents = it.documents + url) }

    private fun updateMedia(transform: (MediaData) -> MediaData) {
        _uiState.value = _uiState.value.copy(
            media = transform(_uiState.value.media),
            stepProgress = computeProgress(4)
        )
    }

    fun isMediaStepComplete(): Boolean {
        val m = _uiState.value.media
        return m.exteriorPhotos.size >= KPNConstants.MIN_EXTERIOR_PHOTOS &&
                m.internalPhotos.size >= KPNConstants.MIN_INTERNAL_PHOTOS &&
                m.competitorPhotos.size >= KPNConstants.MIN_COMPETITOR_PHOTOS &&
                m.videos.size >= KPNConstants.MIN_VIDEOS
    }

    // ─── Step 5: Commercials ─────────────────────────

    fun updateCommercials(commercials: Commercials) {
        val metrics = if (commercials.revenueEstimate > 0) {
            calculateRentMetrics.execute(
                carpetArea = _uiState.value.storeSpecs.carpetArea,
                landlordRent = commercials.landlordRentPerSqft,
                offeredRent = commercials.bdOfferedRentPerSqft,
                revenueEstimate = commercials.revenueEstimate
            )
        } else null

        // Auto-calculate opening month and quarter from possession date
        val updatedCommercials = if (commercials.possessionDate != null) {
            val openingMs = commercials.possessionDate + (KPNConstants.OPENING_MONTH_OFFSET_DAYS * 24L * 60 * 60 * 1000)
            commercials.copy(
                openingMonthAuto = openingMs,
                openingQuarter = deriveQuarter(openingMs),
                totalMonthlyRentAsk = (commercials.landlordRentPerSqft * _uiState.value.storeSpecs.carpetArea).toLong(),
                totalMonthlyRentOffered = (commercials.bdOfferedRentPerSqft * _uiState.value.storeSpecs.carpetArea).toLong(),
                rentAskPercent = metrics?.rentAskPercent ?: 0f,
                rentOfferedPercent = metrics?.rentOfferedPercent ?: 0f,
                rrr = metrics?.rrr ?: 0f
            )
        } else {
            commercials.copy(
                totalMonthlyRentAsk = (commercials.landlordRentPerSqft * _uiState.value.storeSpecs.carpetArea).toLong(),
                totalMonthlyRentOffered = (commercials.bdOfferedRentPerSqft * _uiState.value.storeSpecs.carpetArea).toLong(),
                rentAskPercent = metrics?.rentAskPercent ?: 0f,
                rentOfferedPercent = metrics?.rentOfferedPercent ?: 0f,
                rrr = metrics?.rrr ?: 0f
            )
        }

        _uiState.value = _uiState.value.copy(
            commercials = updatedCommercials,
            rentMetrics = metrics,
            deviations = deviationCheck.check(updatedCommercials),
            stepProgress = computeProgress(5)
        )
    }

    private fun deriveQuarter(epochMs: Long): String {
        // Minimal quarter derivation from epoch without java.time
        val totalDays = epochMs / (24L * 60 * 60 * 1000)
        var y = 1970
        var rem = totalDays.toInt()
        while (true) {
            val diy = if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) 366 else 365
            if (rem < diy) break; rem -= diy; y++
        }
        val ml = intArrayOf(31, if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var m = 0
        while (m < 12 && rem >= ml[m]) { rem -= ml[m]; m++ }
        val q = (m / 3) + 1
        return "Q$q FY$y"
    }

    // ─── Step 6: Competition ─────────────────────────

    fun addCompetitor() {
        val id = "comp_${_uiState.value.competitors.size + 1}"
        _uiState.value = _uiState.value.copy(
            competitors = _uiState.value.competitors + Competitor(id = id)
        )
    }

    fun updateCompetitor(index: Int, competitor: Competitor) {
        val list = _uiState.value.competitors.toMutableList()
        if (index in list.indices) {
            list[index] = competitor
            _uiState.value = _uiState.value.copy(competitors = list)
        }
    }

    fun deleteCompetitor(index: Int) {
        val list = _uiState.value.competitors.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _uiState.value = _uiState.value.copy(competitors = list)
        }
    }

    // ─── Step 7: Contact ─────────────────────────────

    fun updateContact(contact: ContactInfo) {
        _uiState.value = _uiState.value.copy(
            contact = contact,
            stepProgress = computeProgress(7)
        )
    }

    // ─── Submission ──────────────────────────────────

    fun submitProperty(bdExecutiveId: String) {
        val errors = validateAll()
        if (errors.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(validationErrors = errors)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)
            try {
                val state = _uiState.value
                // Create or update
                val property = if (state.savedPropertyId == null) {
                    propertyRepository.createProperty(
                        CreatePropertyRequest(
                            bdExecutiveId = bdExecutiveId,
                            location = state.location
                        )
                    )
                } else {
                    propertyRepository.updateProperty(
                        state.savedPropertyId,
                        UpdatePropertyRequest(
                            location = state.location,
                            storeSpecs = state.storeSpecs,
                            roadAccess = state.roadAccess,
                            commercials = state.commercials,
                            media = state.media,
                            competitors = state.competitors,
                            contact = state.contact
                        )
                    )
                }
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    isSubmitted = true,
                    propertyId = property.propertyId,
                    savedPropertyId = property.propertyId
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    error = e.message ?: "Submission failed. Please try again."
                )
            }
        }
    }

    // ─── Validation ──────────────────────────────────

    fun validateCurrentStep(): Map<String, String> = when (_uiState.value.currentStep) {
        1 -> validateStep1()
        2 -> validateStep2()
        3 -> validateStep3()
        4 -> validateStep4()
        5 -> validateStep5()
        6 -> validateStep6()
        7 -> validateStep7()
        else -> emptyMap()
    }

    private fun validateStep1(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val loc = _uiState.value.location
        if (loc.gpsLat == null || loc.gpsLng == null) errors["gps"] = "GPS location is required"
        if (loc.fullAddress.isBlank()) errors["fullAddress"] = "Full address is required"
        if (loc.city.isBlank()) errors["city"] = "City is required"
        if (loc.state.isBlank()) errors["state"] = "State is required"
        if (loc.pincode.isBlank()) errors["pincode"] = "Pincode is required"
        return errors
    }

    private fun validateStep2(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val s = _uiState.value.storeSpecs
        if (s.totalArea <= 0) errors["totalArea"] = "Total area is required"
        if (s.carpetArea <= 0) errors["carpetArea"] = "Carpet area is required"
        if (s.storeFrontage <= 0f) errors["storeFrontage"] = "Store frontage is required"
        if (s.roadWidth <= 0f) errors["roadWidth"] = "Road width is required"
        return errors
    }

    private fun validateStep3(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val r = _uiState.value.roadAccess
        if (r.frontRoadWidth <= 0f) errors["frontRoadWidth"] = "Front road width is required"
        if (r.carParkingCount < KPNConstants.MIN_CAR_PARKING) {
            errors["carParking"] = "Minimum ${KPNConstants.MIN_CAR_PARKING} car parking spots required"
        }
        return errors
    }

    private fun validateStep4(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val m = _uiState.value.media
        if (m.exteriorPhotos.size < KPNConstants.MIN_EXTERIOR_PHOTOS)
            errors["exteriorPhotos"] = "Minimum ${KPNConstants.MIN_EXTERIOR_PHOTOS} exterior photos required"
        if (m.internalPhotos.size < KPNConstants.MIN_INTERNAL_PHOTOS)
            errors["internalPhotos"] = "Minimum ${KPNConstants.MIN_INTERNAL_PHOTOS} internal photos required"
        if (m.competitorPhotos.size < KPNConstants.MIN_COMPETITOR_PHOTOS)
            errors["competitorPhotos"] = "Minimum ${KPNConstants.MIN_COMPETITOR_PHOTOS} competitor photos required"
        if (m.videos.size < KPNConstants.MIN_VIDEOS)
            errors["videos"] = "Minimum ${KPNConstants.MIN_VIDEOS} videos required"
        return errors
    }

    private fun validateStep5(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val c = _uiState.value.commercials
        if (c.landlordRentPerSqft <= 0f) errors["landlordRent"] = "Landlord rent is required"
        if (c.revenueEstimate <= 0L) errors["revenueEstimate"] = "Revenue estimate is required"
        return errors
    }

    private fun validateStep6(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        _uiState.value.competitors.forEachIndexed { i, c ->
            if (c.brandName.isBlank()) errors["competitor_${i}_brand"] = "Brand name required"
            if (c.distanceMeters <= 0) errors["competitor_${i}_distance"] = "Distance required"
        }
        return errors
    }

    private fun validateStep7(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val c = _uiState.value.contact
        if (c.landlordName.isBlank()) errors["landlordName"] = "Landlord name is required"
        if (c.landlordPhone.length != 10) errors["landlordPhone"] = "Enter a valid 10-digit phone number"
        return errors
    }

    private fun validateAll(): Map<String, String> {
        return validateStep1() + validateStep2() + validateStep3() +
                validateStep4() + validateStep5() + validateStep6() + validateStep7()
    }

    // ─── Progress calculation ─────────────────────────

    private fun computeProgress(step: Int): Float = when (step) {
        1 -> {
            val loc = _uiState.value.location
            val filled = listOf(loc.gpsLat != null, loc.fullAddress.isNotBlank(), loc.city.isNotBlank(), loc.pincode.isNotBlank())
            filled.count { it } / filled.size.toFloat()
        }
        2 -> {
            val s = _uiState.value.storeSpecs
            val filled = listOf(s.totalArea > 0, s.carpetArea > 0, s.storeFrontage > 0f, s.floors.isNotEmpty())
            filled.count { it } / filled.size.toFloat()
        }
        3 -> {
            val r = _uiState.value.roadAccess
            val filled = listOf(r.frontRoadWidth > 0f, r.carParkingCount >= KPNConstants.MIN_CAR_PARKING)
            filled.count { it } / filled.size.toFloat()
        }
        4 -> {
            val m = _uiState.value.media
            val filled = listOf(
                m.exteriorPhotos.size >= KPNConstants.MIN_EXTERIOR_PHOTOS,
                m.internalPhotos.size >= KPNConstants.MIN_INTERNAL_PHOTOS,
                m.competitorPhotos.size >= KPNConstants.MIN_COMPETITOR_PHOTOS,
                m.videos.size >= KPNConstants.MIN_VIDEOS
            )
            filled.count { it } / filled.size.toFloat()
        }
        5 -> {
            val c = _uiState.value.commercials
            val filled = listOf(c.landlordRentPerSqft > 0f, c.revenueEstimate > 0L, c.possessionDate != null)
            filled.count { it } / filled.size.toFloat()
        }
        6 -> if (_uiState.value.competitors.isNotEmpty()) 0.5f else 0f
        7 -> {
            val c = _uiState.value.contact
            val filled = listOf(c.landlordName.isNotBlank(), c.landlordPhone.length == 10)
            filled.count { it } / filled.size.toFloat()
        }
        else -> 0f
    }
}
