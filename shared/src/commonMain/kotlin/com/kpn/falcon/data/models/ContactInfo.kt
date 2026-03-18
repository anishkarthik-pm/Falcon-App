package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ContactInfo(
    val landlordName: String = "",
    val landlordPhone: String = "",
    val landlordEmail: String? = null,
    val brokerName: String? = null,
    val brokerPhone: String? = null,
    val source: ContactSource = ContactSource.DIRECT
)

@Serializable
enum class ContactSource {
    BROKER, DIRECT, SELF_SOURCED
}
