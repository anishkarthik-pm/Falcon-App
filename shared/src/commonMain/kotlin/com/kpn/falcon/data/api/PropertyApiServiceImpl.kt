package com.kpn.falcon.data.api

import com.kpn.falcon.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class PropertyApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : PropertyApiService {

    override suspend fun createProperty(body: CreatePropertyRequest): PropertyLead {
        return httpClient.post("$baseUrl/properties") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    override suspend fun updateProperty(id: String, body: UpdatePropertyRequest): PropertyLead {
        return httpClient.patch("$baseUrl/properties/$id") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    override suspend fun getProperties(filter: PropertyFilter): List<PropertyLead> {
        return httpClient.get("$baseUrl/properties") {
            filter.status?.let { parameter("status", it.name) }
            filter.bdExecutiveId?.let { parameter("bdExecutiveId", it) }
            filter.city?.let { parameter("city", it) }
            filter.searchQuery?.let { parameter("q", it) }
        }.body()
    }

    override suspend fun getPropertyById(id: String): PropertyLead {
        return httpClient.get("$baseUrl/properties/$id").body()
    }

    override suspend fun deleteProperty(id: String) {
        httpClient.delete("$baseUrl/properties/$id")
    }

    override suspend fun uploadMedia(id: String, file: ByteArray, type: MediaType): MediaUploadResponse {
        return httpClient.post("$baseUrl/properties/$id/media") {
            setBody(MultiPartFormDataContent(
                formData {
                    append("type", type.name)
                    append("file", file, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=upload")
                    })
                }
            ))
        }.body()
    }

    override suspend fun uploadGeoIQPdf(id: String, pdf: ByteArray): GeoIQParseResponse {
        return httpClient.post("$baseUrl/properties/$id/geoiq") {
            setBody(MultiPartFormDataContent(
                formData {
                    append("pdf", pdf, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=geoiq.pdf")
                        append(HttpHeaders.ContentType, "application/pdf")
                    })
                }
            ))
        }.body()
    }

    override suspend fun submitApproval(id: String, action: ApprovalAction): PropertyLead {
        return httpClient.post("$baseUrl/properties/$id/approval") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("action" to action.name))
        }.body()
    }

    override suspend fun getDashboardStats(): DashboardStats {
        return httpClient.get("$baseUrl/dashboard/stats").body()
    }

    override suspend fun getNotifications(): List<NotificationItem> {
        return httpClient.get("$baseUrl/notifications").body()
    }
}
