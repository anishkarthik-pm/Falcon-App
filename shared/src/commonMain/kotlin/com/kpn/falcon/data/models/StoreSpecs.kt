package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class StoreSpecs(
    val siteStatus: SiteStatus = SiteStatus.READY_TO_MOVE,
    val roadFacing: RoadFacing = RoadFacing.MAIN,
    val floors: List<FloorType> = emptyList(),
    val totalArea: Int = 0,
    val carpetArea: Int = 0,
    val stepsToEntry: Int = 0,
    val ceilingHeight: Float = 0f,
    val storeFrontage: Float = 0f,
    val facadeFrontage: Float = 0f,
    val storeDimensions: String = "",
    val infrastructure: List<InfraType> = emptyList(),
    val signageAvailability: SignageType = SignageType.NOT_AVAILABLE,
    val signageWidth: Float? = null,
    val additionalSignageRequired: Int? = null,
    val juiceCounterAvailable: Boolean = false,
    val juiceCounterArea: Float? = null,
    val powerLoad: Float? = null,
    val roadWidth: Float = 0f,
    val carParking: Int = 0,
    val bikeParking: Int = 0
)

@Serializable
enum class SiteStatus {
    READY_TO_MOVE, BTS, UNDER_CONSTRUCTION, FITOUT
}

@Serializable
enum class RoadFacing {
    MAIN, SIDE
}

@Serializable
enum class FloorType {
    GROUND, STILT, FIRST, SECOND, UNDERGROUND
}

@Serializable
enum class InfraType {
    AC, ELECTRICAL_WIRING, CCTV, PLUMBING, FIRE_SAFETY, FLOORING, TOILET
}

@Serializable
enum class SignageType {
    AVAILABLE, PARTIALLY_AVAILABLE, NOT_AVAILABLE
}
