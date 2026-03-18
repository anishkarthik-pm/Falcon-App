package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Commercials(
    val landlordRentPerSqft: Float = 0f,
    val bdOfferedRentPerSqft: Float = 0f,
    val revenueEstimate: Long = 0L,
    val totalMonthlyRentAsk: Long = 0L,
    val totalMonthlyRentOffered: Long = 0L,
    val rentAskPercent: Float = 0f,
    val rentOfferedPercent: Float = 0f,
    val leaseTermYears: Int = 15,
    val escalationPercent: Float = 15f,
    val escalationFrequencyYears: Int = 3,
    val securityDepositMonths: Int = 0,
    val rentFreePeriodDays: Int = 0,
    val lesseeLockInYears: Int = 0,
    val lessorLockIn: String = "Entire term",
    val registrationFees: RegistrationFees = RegistrationFees.EQUALLY_SHARED,
    val possessionDate: Long? = null,
    val openingMonthAuto: Long? = null,
    val openingQuarter: String? = null,
    val llScopeLineItems: List<LLScopeItem> = emptyList(),
    val llScopeTotal: Long = 0L,
    val perSqftAddRent: Float = 0f,
    val rrr: Float = 0f,
    val deviationFromStdTerms: String? = null,
    val adjustableAdvance: Long? = null,
    val marketRentPerSqft: Float? = null,
    val atlSignedDate: Long? = null,
    val leaseSignedDate: Long? = null
)

@Serializable
enum class RegistrationFees {
    EQUALLY_SHARED, LANDLORD_BEARS, KPN_BEARS
}

@Serializable
data class LLScopeItem(
    val description: String,
    val amount: Long
)
