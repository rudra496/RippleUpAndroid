package com.yft.rippleup.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single per-user stats row (id = 0). v2 adds production guard columns:
 * cooldown timestamps, daily counters, and an HMAC integrity tag.
 */
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 0,
    val userName: String = "Friend",
    val points: Int = 0,
    val co2SavedKg: Double = 0.0,
    val streak: Int = 0,
    val ecoActions: Int = 0,
    val lastActionEpochDay: Long = -1L,
    val onboarded: Boolean = false,
    // --- v2: guard rails ---
    val lastRefillAt: Long = 0L,
    val lastRecycleAt: Long = 0L,
    val lastFoodAt: Long = 0L,
    val lastTransitAt: Long = 0L,
    val lastScanCampusAt: Long = 0L,
    val lastScanGrocerAt: Long = 0L,
    val lastScanBinAt: Long = 0L,
    val dailyEpochDay: Long = -1L,   // which day the counters below belong to
    val actionsToday: Int = 0,
    val pointsToday: Int = 0,
    val integrityTag: String = "",   // HMAC over the fields above (tamper detection)
)

/** One row per verified eco action (audit log). */
@Entity(tableName = "activity_log")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val points: Int,
    val co2Kg: Double,
    val colorTag: String,
    val iconTag: String,
    val timestamp: Long,
)
