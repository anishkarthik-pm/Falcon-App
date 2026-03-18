package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaData(
    val exteriorPhotos: List<String> = emptyList(),
    val internalPhotos: List<String> = emptyList(),
    val competitorPhotos: List<String> = emptyList(),
    val videos: List<String> = emptyList(),
    val documents: List<String> = emptyList()
)

@Serializable
data class Competitor(
    val id: String = "",
    val brandName: String = "",
    val distanceMeters: Int = 0,
    val storeAreaSqft: Int = 0,
    val rentPerSqft: Float? = null,
    val salesPerMonth: Long? = null,
    val photos: List<String> = emptyList()
)

@Serializable
data class Community(
    val name: String,
    val type: String,
    val distanceMeters: Int
)

@Serializable
data class ApprovalEvent(
    val eventId: String,
    val actorId: String,
    val actorRole: String,
    val action: ApprovalAction,
    val remarks: String? = null,
    val timestamp: Long
)

@Serializable
enum class ApprovalAction {
    SUBMIT, APPROVE, REJECT, FORWARD, REQUEST_REVISION
}

@Serializable
data class DashboardStats(
    val openProperties: Int = 0,
    val pendingProperties: Int = 0,
    val closedProperties: Int = 0,
    val inProgressProperties: Int = 0
)

@Serializable
data class NotificationItem(
    val id: String,
    val propertyId: String,
    val title: String,
    val message: String,
    val status: PropertyStatus,
    val city: String,
    val state: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

@Serializable
data class ActivityItem(
    val propertyId: String,
    val action: String,
    val timestamp: Long
)
