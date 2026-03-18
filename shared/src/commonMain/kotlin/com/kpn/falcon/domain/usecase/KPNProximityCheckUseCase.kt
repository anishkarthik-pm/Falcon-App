package com.kpn.falcon.domain.usecase

import com.kpn.falcon.data.models.KPNProximityResult
import com.kpn.falcon.util.KPNConstants
import kotlin.math.*

data class KPNStore(val name: String, val lat: Double, val lng: Double)

class KPNProximityCheckUseCase {

    fun check(lat: Double, lng: Double, stores: List<KPNStore>): KPNProximityResult? {
        if (stores.isEmpty()) return null

        val nearest = stores.minByOrNull { haversineKm(lat, lng, it.lat, it.lng) } ?: return null
        val distanceKm = haversineKm(lat, lng, nearest.lat, nearest.lng)

        return KPNProximityResult(
            nearestStoreName = nearest.name,
            distanceKm = distanceKm,
            isWithin3km = distanceKm < KPNConstants.KPN_PROXIMITY_RADIUS_KM
        )
    }

    private fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = (lat2 - lat1).toRadians()
        val dLng = (lng2 - lng1).toRadians()
        val a = sin(dLat / 2).pow(2) +
                cos(lat1.toRadians()) * cos(lat2.toRadians()) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun Double.toRadians() = this * PI / 180
}
