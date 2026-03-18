package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RoadAccess(
    val frontRoadWidth: Float = 0f,
    val roadType: RoadType = RoadType.MAIN_ROAD,
    val roadConnectivity: List<ConnectivityType> = emptyList(),
    val parkingAvailable: Boolean = false,
    val deliveryVehicleAccess: AccessDifficulty = AccessDifficulty.EASY,
    val carParkingCount: Int = 0,
    val bikeParkingCount: Int = 0
)

@Serializable
enum class RoadType {
    MAIN_ROAD, RING_ROAD, INTERIOR_ROAD, SERVICE_ROAD
}

@Serializable
enum class ConnectivityType {
    BUS_ROUTE, METRO_NEARBY, HIGHWAY_ACCESS
}

@Serializable
enum class AccessDifficulty {
    EASY, MODERATE, DIFFICULT
}
