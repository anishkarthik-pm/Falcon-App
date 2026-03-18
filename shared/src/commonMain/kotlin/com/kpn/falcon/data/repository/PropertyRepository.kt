package com.kpn.falcon.data.repository

import com.kpn.falcon.data.api.*
import com.kpn.falcon.data.models.*
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(filter: PropertyFilter): Flow<List<PropertyLead>>
    suspend fun getPropertyById(id: String): PropertyLead
    suspend fun createProperty(request: CreatePropertyRequest): PropertyLead
    suspend fun updateProperty(id: String, request: UpdatePropertyRequest): PropertyLead
    suspend fun deleteProperty(id: String)
    suspend fun uploadMedia(id: String, file: ByteArray, type: MediaType): MediaUploadResponse
    suspend fun uploadGeoIQPdf(id: String, pdf: ByteArray): GeoIQParseResponse
    suspend fun submitApproval(id: String, action: ApprovalAction): PropertyLead
    suspend fun getDashboardStats(): DashboardStats
    suspend fun getNotifications(): List<NotificationItem>
    suspend fun syncProperties()
}
