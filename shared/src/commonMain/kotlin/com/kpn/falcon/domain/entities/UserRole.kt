package com.kpn.falcon.domain.entities

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    BD_EXECUTIVE, BD_MANAGER, STATE_HEAD, BD_HEAD, CEO, CFO
}

@Serializable
data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val email: String,
    val avatarUrl: String? = null,
    val stateCode: String? = null
)
