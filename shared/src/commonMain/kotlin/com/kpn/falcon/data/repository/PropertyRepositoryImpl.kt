package com.kpn.falcon.data.repository

import com.kpn.falcon.data.api.*
import com.kpn.falcon.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PropertyRepositoryImpl(
    private val apiService: PropertyApiService
) : PropertyRepository {

    override fun getProperties(filter: PropertyFilter): Flow<List<PropertyLead>> = flow {
        val properties = apiService.getProperties(filter)
        emit(properties)
    }

    override suspend fun getPropertyById(id: String): PropertyLead {
        return apiService.getPropertyById(id)
    }

    override suspend fun createProperty(request: CreatePropertyRequest): PropertyLead {
        return apiService.createProperty(request)
    }

    override suspend fun updateProperty(id: String, request: UpdatePropertyRequest): PropertyLead {
        return apiService.updateProperty(id, request)
    }

    override suspend fun deleteProperty(id: String) {
        apiService.deleteProperty(id)
    }

    override suspend fun uploadMedia(id: String, file: ByteArray, type: MediaType): MediaUploadResponse {
        return apiService.uploadMedia(id, file, type)
    }

    override suspend fun uploadGeoIQPdf(id: String, pdf: ByteArray): GeoIQParseResponse {
        return apiService.uploadGeoIQPdf(id, pdf)
    }

    override suspend fun submitApproval(id: String, action: ApprovalAction): PropertyLead {
        return apiService.submitApproval(id, action)
    }

    override suspend fun getDashboardStats(): DashboardStats {
        return apiService.getDashboardStats()
    }

    override suspend fun getNotifications(): List<NotificationItem> {
        return apiService.getNotifications()
    }

    override suspend fun syncProperties() {
        // Full sync — fetch all and cache via SQLDelight (wired up in Task 11)
        apiService.getProperties(PropertyFilter())
    }
}
