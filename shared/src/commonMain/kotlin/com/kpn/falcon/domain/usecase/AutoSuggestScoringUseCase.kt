package com.kpn.falcon.domain.usecase

import com.kpn.falcon.data.models.ScoringLookup

data class SuggestedScore(
    val parameter: String,
    val rawValue: Int,
    val suggestedScore: Int?,
    val lookupTable: Map<Int, Int>
)

class AutoSuggestScoringUseCase {
    fun execute(
        area: Int,
        frontage: Int,
        carParking: Int,
        bikeParking: Int,
        stepsToEntry: Int,
        roadWidth: Int,
        juiceCounterAvailable: Boolean
    ): List<SuggestedScore> {
        return listOf(
            SuggestedScore("area", area, ScoringLookup.lookupScore(ScoringLookup.area, area), ScoringLookup.area),
            SuggestedScore("frontage", frontage, ScoringLookup.lookupScore(ScoringLookup.frontage, frontage), ScoringLookup.frontage),
            SuggestedScore("carParking", carParking, ScoringLookup.lookupScore(ScoringLookup.carParking, carParking), ScoringLookup.carParking),
            SuggestedScore("bikeParking", bikeParking, ScoringLookup.lookupScore(ScoringLookup.bikeParking, bikeParking), ScoringLookup.bikeParking),
            SuggestedScore("juiceCounter", if (juiceCounterAvailable) 1 else 0, if (juiceCounterAvailable) 5 else 0, emptyMap()),
            SuggestedScore("stepsToEntry", stepsToEntry, ScoringLookup.lookupScore(ScoringLookup.stepsToEntry, stepsToEntry), ScoringLookup.stepsToEntry),
            SuggestedScore("roadWidth", roadWidth, ScoringLookup.lookupScore(ScoringLookup.roadWidth, roadWidth), ScoringLookup.roadWidth)
        )
    }
}
