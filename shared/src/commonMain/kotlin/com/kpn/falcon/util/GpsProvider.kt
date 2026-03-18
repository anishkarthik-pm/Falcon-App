package com.kpn.falcon.util

data class GpsCoordinates(val lat: Double, val lng: Double)

expect class GpsProvider {
    suspend fun getCurrentLocation(): GpsCoordinates?
    fun hasLocationPermission(): Boolean
}
