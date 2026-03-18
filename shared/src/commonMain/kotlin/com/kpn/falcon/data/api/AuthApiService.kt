package com.kpn.falcon.data.api

import com.kpn.falcon.domain.entities.User
import kotlinx.serialization.Serializable

interface AuthApiService {
    suspend fun login(email: String, password: String): LoginResponse
    suspend fun getMe(): User
    suspend fun logout()
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)
