package com.kpn.falcon.util

import com.kpn.falcon.data.models.RegistrationFees

object KPNConstants {
    // Store proximity
    const val KPN_PROXIMITY_RADIUS_KM = 3.0

    // Parking minimums
    const val MIN_CAR_PARKING = 4
    const val MIN_BIKE_PARKING = 5

    // Carpet area efficiency threshold (70%)
    const val CARPET_AREA_EFFICIENCY_THRESHOLD = 0.70f

    // Media upload minimums
    const val MIN_EXTERIOR_PHOTOS = 4
    const val MIN_INTERNAL_PHOTOS = 3
    const val MIN_COMPETITOR_PHOTOS = 3
    const val MIN_VIDEOS = 3

    // KPN Standard lease terms
    const val DEFAULT_LEASE_TERM_YEARS = 15
    const val DEFAULT_ESCALATION_PERCENT = 15f
    const val DEFAULT_ESCALATION_FREQUENCY_YEARS = 3
    const val DEFAULT_LESSOR_LOCK_IN = "Entire term"
    val DEFAULT_REGISTRATION_FEES = RegistrationFees.EQUALLY_SHARED

    // Opening month offset
    const val OPENING_MONTH_OFFSET_DAYS = 30

    // GeoIQ SLA (2 working days from lead gen date)
    const val GEO_IQ_SLA_WORKING_DAYS = 2

    // Scoring parameter weights
    const val WEIGHT_AREA = 0.30f
    const val WEIGHT_FRONTAGE = 0.20f
    const val WEIGHT_CAR_PARKING = 0.10f
    const val WEIGHT_BIKE_PARKING = 0.10f
    const val WEIGHT_JUICE_COUNTER = 0.10f
    const val WEIGHT_STEPS_TO_ENTRY = 0.10f
    const val WEIGHT_ROAD_WIDTH = 0.10f

    // Network
    const val REQUEST_TIMEOUT_MS = 30_000L
    const val CONNECT_TIMEOUT_MS = 15_000L

    // Competition mapping
    const val MIN_COMPETITORS = 3
    const val MAX_COMPETITORS = 5
}
