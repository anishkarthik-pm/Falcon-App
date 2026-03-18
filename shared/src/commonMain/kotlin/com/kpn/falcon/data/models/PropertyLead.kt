package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PropertyLead(
    val propertyId: String,
    val bdExecutiveId: String,
    val bdManagerId: String? = null,
    val stateHeadId: String? = null,
    val status: PropertyStatus = PropertyStatus.DRAFT,
    val phase: Int = 1,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val location: LocationData = LocationData(),
    val storeSpecs: StoreSpecs = StoreSpecs(),
    val roadAccess: RoadAccess = RoadAccess(),
    val commercials: Commercials = Commercials(),
    val media: MediaData = MediaData(),
    val competitors: List<Competitor> = emptyList(),
    val communities: List<Community> = emptyList(),
    val geoIq: GeoIQData? = null,
    val scoring: ScoringData? = null,
    val contact: ContactInfo = ContactInfo(),
    val approvalChain: List<ApprovalEvent> = emptyList()
)

@Serializable
enum class PropertyStatus {
    DRAFT, SUBMITTED, IN_REVIEW, FORWARDED, APPROVED, REJECTED
}
