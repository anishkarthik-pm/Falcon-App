package com.kpn.falcon.domain.usecase

import com.kpn.falcon.data.models.Commercials
import com.kpn.falcon.data.models.RegistrationFees
import com.kpn.falcon.util.KPNConstants

data class DeviationResult(
    val field: String,
    val actualValue: String,
    val standardValue: String
)

class DeviationCheckUseCase {

    fun check(commercials: Commercials): List<DeviationResult> {
        val deviations = mutableListOf<DeviationResult>()

        if (commercials.leaseTermYears != KPNConstants.DEFAULT_LEASE_TERM_YEARS) {
            deviations += DeviationResult(
                field = "leaseTermYears",
                actualValue = "${commercials.leaseTermYears} years",
                standardValue = "${KPNConstants.DEFAULT_LEASE_TERM_YEARS} years"
            )
        }

        if (commercials.escalationPercent != KPNConstants.DEFAULT_ESCALATION_PERCENT) {
            deviations += DeviationResult(
                field = "escalationPercent",
                actualValue = "${commercials.escalationPercent}%",
                standardValue = "${KPNConstants.DEFAULT_ESCALATION_PERCENT}%"
            )
        }

        if (commercials.escalationFrequencyYears != KPNConstants.DEFAULT_ESCALATION_FREQUENCY_YEARS) {
            deviations += DeviationResult(
                field = "escalationFrequencyYears",
                actualValue = "per ${commercials.escalationFrequencyYears} years",
                standardValue = "per ${KPNConstants.DEFAULT_ESCALATION_FREQUENCY_YEARS} years"
            )
        }

        if (commercials.lessorLockIn != KPNConstants.DEFAULT_LESSOR_LOCK_IN) {
            deviations += DeviationResult(
                field = "lessorLockIn",
                actualValue = commercials.lessorLockIn,
                standardValue = KPNConstants.DEFAULT_LESSOR_LOCK_IN
            )
        }

        if (commercials.registrationFees != KPNConstants.DEFAULT_REGISTRATION_FEES) {
            deviations += DeviationResult(
                field = "registrationFees",
                actualValue = commercials.registrationFees.name,
                standardValue = KPNConstants.DEFAULT_REGISTRATION_FEES.name
            )
        }

        return deviations
    }
}
