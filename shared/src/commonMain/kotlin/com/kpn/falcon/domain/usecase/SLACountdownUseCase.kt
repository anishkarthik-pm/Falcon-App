package com.kpn.falcon.domain.usecase

import kotlin.math.abs

class SLACountdownUseCase {

    /**
     * Returns SLA deadline = leadGenDate + 2 working days (skip Saturday/Sunday).
     * Uses epoch milliseconds.
     */
    fun getDeadline(leadGenDate: Long): Long {
        var workingDaysAdded = 0
        var currentMs = leadGenDate
        val oneDayMs = 24L * 60 * 60 * 1000

        while (workingDaysAdded < 2) {
            currentMs += oneDayMs
            val dayOfWeek = getDayOfWeek(currentMs)
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDaysAdded++
            }
        }
        return currentMs
    }

    fun getHoursRemaining(deadline: Long): Long {
        val now = currentTimeMillis()
        return (deadline - now) / (60 * 60 * 1000)
    }

    fun isBreached(deadline: Long): Boolean {
        return currentTimeMillis() > deadline
    }

    private enum class DayOfWeek { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }

    /**
     * Returns the day of week for a given epoch milliseconds value.
     * Based on Jan 1 1970 being a Thursday.
     */
    private fun getDayOfWeek(epochMs: Long): DayOfWeek {
        val days = (epochMs / (24L * 60 * 60 * 1000)).toInt()
        // Day 0 (Jan 1 1970) = Thursday = index 3 (0=Mon, 3=Thu, 5=Sat, 6=Sun)
        val dayIndex = ((days % 7) + 3) % 7
        return DayOfWeek.entries[dayIndex]
    }
}

expect fun currentTimeMillis(): Long
