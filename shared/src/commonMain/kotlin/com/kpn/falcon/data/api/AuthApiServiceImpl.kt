package com.kpn.falcon.data.api

import com.kpn.falcon.domain.entities.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : AuthApiService {

    override suspend fun login(email: String, password: String): LoginResponse {
        return httpClient.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }

    override suspend fun getMe(): User {
        return httpClient.get("$baseUrl/auth/me").body()
    }

    override suspend fun logout() {
        httpClient.post("$baseUrl/auth/logout")
    }
}
