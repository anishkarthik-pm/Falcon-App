package com.kpn.falcon.data.api

import com.kpn.falcon.data.models.*

interface PropertyApiService {
    suspend fun createProperty(body: CreatePropertyRequest): PropertyLead
    suspend fun updateProperty(id: String, body: UpdatePropertyRequest): PropertyLead
    suspend fun getProperties(filter: PropertyFilter): List<PropertyLead>
    suspend fun getPropertyById(id: String): PropertyLead
    suspend fun deleteProperty(id: String)
    suspend fun uploadMedia(id: String, file: ByteArray, type: MediaType): MediaUploadResponse
    suspend fun uploadGeoIQPdf(id: String, pdf: ByteArray): GeoIQParseResponse
    suspend fun submitApproval(id: String, action: ApprovalAction): PropertyLead
    suspend fun getDashboardStats(): DashboardStats
    suspend fun getNotifications(): List<NotificationItem>
}

data class CreatePropertyRequest(
    val bdExecutiveId: String,
    val location: com.kpn.falcon.data.models.LocationData
)

data class UpdatePropertyRequest(
    val location: com.kpn.falcon.data.models.LocationData? = null,
    val storeSpecs: com.kpn.falcon.data.models.StoreSpecs? = null,
    val roadAccess: com.kpn.falcon.data.models.RoadAccess? = null,
    val commercials: com.kpn.falcon.data.models.Commercials? = null,
    val media: com.kpn.falcon.data.models.MediaData? = null,
    val competitors: List<com.kpn.falcon.data.models.Competitor>? = null,
    val contact: com.kpn.falcon.data.models.ContactInfo? = null
)

data class PropertyFilter(
    val status: com.kpn.falcon.data.models.PropertyStatus? = null,
    val bdExecutiveId: String? = null,
    val city: String? = null,
    val searchQuery: String? = null
)

enum class MediaType {
    EXTERIOR_PHOTO, INTERNAL_PHOTO, COMPETITOR_PHOTO, VIDEO, DOCUMENT
}

data class MediaUploadResponse(
    val url: String,
    val mediaType: MediaType,
    val uploadedAt: Long
)

data class GeoIQParseResponse(
    val success: Boolean,
    val geoIq: com.kpn.falcon.data.models.GeoIQData
)
