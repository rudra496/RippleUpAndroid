package com.yft.rippleup.util

import java.time.LocalDate

/**
 * Streak arithmetic. A streak increments on the first action of a new calendar
 * day. If the gap since the last action is > 1 day, the streak resets to 1.
 * (The web demo simply increments on every action; we make it a *real* daily
 * streak which is more meaningful in a persistent app.)
 */
object Streak {

    fun nextStreak(lastEpochDay: Long, now: LocalDate = LocalDate.now()): Int {
        if (lastEpochDay < 0) return 1 // first ever action
        val today = now.toEpochDay()
        return when (today - lastEpochDay) {
            0L -> 0   // same day — no streak bump, just an action
            1L -> -1  // signal "increment existing streak by 1" handled by caller
            else -> 1 // streak broken — restart
        }
    }

    /**
     * Apply streak rules and return the new streak count.
     */
    fun apply(currentStreak: Int, lastEpochDay: Long, now: LocalDate = LocalDate.now()): Int {
        val today = now.toEpochDay()
        return when {
            lastEpochDay < 0 -> 1
            today == lastEpochDay -> currentStreak.coerceAtLeast(1)
            today - lastEpochDay == 1L -> currentStreak + 1
            else -> 1
        }
    }
}
