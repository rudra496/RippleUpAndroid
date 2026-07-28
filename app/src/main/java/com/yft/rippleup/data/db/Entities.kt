package com.yft.rippleup.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single, per-user stats row. There is always exactly one row (id = 0); the
 * repository upserts it. Mirrors the `state` object in the web app.js but
 * persists across launches.
 */
@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 0,
    val userName: String = "Friend",
    val points: Int = 0,
    val co2SavedKg: Double = 0.0,
    val streak: Int = 0,
    val ecoActions: Int = 0,
    val lastActionEpochDay: Long = -1L, // java.time.LocalDate.toEpochDay(), -1 = never
    val onboarded: Boolean = false,
)

/**
 * One row per verified eco action. Newest first by id (auto-increment).
 */
@Entity(tableName = "activity_log")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val points: Int,
    val co2Kg: Double,
    val colorTag: String,   // "blue" | "red" | "green" | "purple"
    val iconTag: String,    // icon name from EcoAction/ScanPreset
    val timestamp: Long,    // System.currentTimeMillis()
)
