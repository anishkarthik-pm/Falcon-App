package com.kpn.falcon.domain.usecase

class PropertyIdGeneratorUseCase {
    /**
     * Format: KPN-{STATE_CODE}-{YEAR}-{SEQ}
     * e.g. KPN-KA-2026-0869
     */
    fun generate(stateCode: String, year: Int, sequence: Int): String {
        val paddedSeq = sequence.toString().padStart(4, '0')
        return "KPN-${stateCode.uppercase()}-$year-$paddedSeq"
    }

    fun stateCodeFromName(stateName: String): String {
        return when (stateName.lowercase().trim()) {
            "karnataka" -> "KA"
            "tamil nadu", "tamilnadu" -> "TN"
            "andhra pradesh" -> "AP"
            "telangana" -> "TG"
            "maharashtra" -> "MH"
            "kerala" -> "KL"
            else -> stateName.take(2).uppercase()
        }
    }
}
