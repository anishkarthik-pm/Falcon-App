package com.kpn.falcon.domain.usecase

class CalculateCompositeScoreUseCase {
    fun execute(
        scoreArea: Int,
        scoreFrontage: Int,
        scoreCarParking: Int,
        scoreBikeParking: Int,
        scoreJuiceCounter: Int,
        scoreStepsToEntry: Int,
        scoreRoadWidth: Int
    ): Float {
        return (scoreArea * 0.30f) +
                (scoreFrontage * 0.20f) +
                (scoreCarParking * 0.10f) +
                (scoreBikeParking * 0.10f) +
                (scoreJuiceCounter * 0.10f) +
                (scoreStepsToEntry * 0.10f) +
                (scoreRoadWidth * 0.10f)
    }
}
