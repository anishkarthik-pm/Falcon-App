package com.kpn.falcon.domain.usecase

data class RentMetrics(
    val totalMonthlyRentAsk: Long,
    val totalMonthlyRentOffered: Long,
    val rentAskPercent: Float,
    val rentOfferedPercent: Float,
    val rrr: Float
)

class CalculateRentMetricsUseCase {
    fun execute(
        carpetArea: Int,
        landlordRent: Float,
        offeredRent: Float,
        revenueEstimate: Long
    ): RentMetrics {
        val totalAsk = (landlordRent * carpetArea).toLong()
        val totalOffered = (offeredRent * carpetArea).toLong()
        val askPercent = if (revenueEstimate > 0)
            (landlordRent * carpetArea / revenueEstimate * 100).toFloat() else 0f
        val offeredPercent = if (revenueEstimate > 0)
            (offeredRent * carpetArea / revenueEstimate * 100).toFloat() else 0f
        val rrr = if (revenueEstimate > 0)
            ((offeredRent * carpetArea) / revenueEstimate).toFloat() else 0f

        return RentMetrics(
            totalMonthlyRentAsk = totalAsk,
            totalMonthlyRentOffered = totalOffered,
            rentAskPercent = askPercent,
            rentOfferedPercent = offeredPercent,
            rrr = rrr
        )
    }
}
