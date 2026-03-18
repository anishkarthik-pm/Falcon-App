package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class GeoIQData(
    val pdfUrl: String? = null,
    val parsedAt: Long? = null,
    val confirmedByManager: Boolean = false,
    val slaDeadline: Long = 0L,
    val hhTotal1km: Int? = null,
    val hhAbove5L1km: Int? = null,
    val hhAbove10L1km: Int? = null,
    val hhAbove20L1km: Int? = null,
    val hhTotal10minWalk: Int? = null,
    val hhAbove5L10min: Int? = null,
    val hhAbove10L10min: Int? = null,
    val hhAbove20L10min: Int? = null,
    val hhTotal2km: Int? = null,
    val nearestKpnStore: String? = null,
    val nearestKpnDistanceKm: Float? = null,
    val affluenceIndex: Float? = null,
    val avgResidentialRentPerSqft: Float? = null,
    val totalFootfallScore: Float? = null,
    val growthTrendScore: Float? = null,
    val spendingCapacity: Float? = null,
    val retailIndex: Float? = null,
    val storesInArea: Int? = null,
    val competitorPresence: CompetitorPresence? = null,
    val cannibalisation: Float? = null,
    val complementaryBrands: List<String> = emptyList(),
    val geoIqOverallRating: Float? = null
)

@Serializable
enum class CompetitorPresence {
    HIGH, MEDIUM, LOW
}
