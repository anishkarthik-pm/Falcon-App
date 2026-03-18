package com.kpn.falcon.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ScoringData(
    val scoreArea: Int? = null,
    val scoreFrontage: Int? = null,
    val scoreCarParking: Int? = null,
    val scoreBikeParking: Int? = null,
    val scoreJuiceCounter: Int? = null,
    val scoreStepsToEntry: Int? = null,
    val scoreRoadWidth: Int? = null,
    val compositeScore: Float? = null,
    val salesProjection: Long? = null,
    val comparableStoreRef: String? = null,
    val strategicNotes: String? = null,
    val confirmedByStateHead: Boolean = false
)

object ScoringLookup {
    val area: Map<Int, Int> = mapOf(
        1500 to 1,
        2000 to 2,
        2500 to 3,
        3000 to 4,
        3500 to 5
    )
    val frontage: Map<Int, Int> = mapOf(
        20 to 1,
        30 to 2,
        40 to 3,
        50 to 4,
        60 to 5
    )
    val carParking: Map<Int, Int> = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 4,
        5 to 5
    )
    val bikeParking: Map<Int, Int> = mapOf(
        5 to 1,
        10 to 2,
        15 to 3,
        20 to 4,
        25 to 5
    )
    val stepsToEntry: Map<Int, Int> = mapOf(
        10 to 1,
        8 to 2,
        5 to 3,
        2 to 4,
        0 to 5
    )
    val roadWidth: Map<Int, Int> = mapOf(
        15 to 1,
        30 to 2,
        45 to 3,
        60 to 4,
        100 to 5
    )

    /**
     * Returns the score for a given raw value using the lookup table.
     * Finds the largest key that is <= rawValue (floor lookup).
     * Returns null if rawValue is below the smallest threshold.
     */
    fun lookupScore(table: Map<Int, Int>, rawValue: Int): Int? {
        return table.keys
            .filter { it <= rawValue }
            .maxOrNull()
            ?.let { table[it] }
    }
}
