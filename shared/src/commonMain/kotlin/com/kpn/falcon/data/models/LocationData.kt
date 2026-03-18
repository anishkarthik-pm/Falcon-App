package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val gpsLat: Double? = null,
    val gpsLng: Double? = null,
    val fullAddress: String = "",
    val nearestLandmark: String? = null,
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val kpnProximityCheck: KPNProximityResult? = null
)

@Serializable
data class KPNProximityResult(
    val nearestStoreName: String,
    val distanceKm: Double,
    val isWithin3km: Boolean
)
