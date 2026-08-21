package com.yft.rippleup.data.repo

import android.content.Context
import com.yft.rippleup.data.db.ActivityLogEntity
import com.yft.rippleup.data.db.AppDatabase
import com.yft.rippleup.data.db.UserStatsEntity
import com.yft.rippleup.data.model.Badge
import com.yft.rippleup.util.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Snapshot of everything the UI needs to render the dashboard, derived from the
 * persistent [UserStatsEntity]. Badges are computed from ecoActions.
 */
data class UserSnapshot(
    val userName: String,
    val points: Int,
    val co2SavedKg: Double,
    val streak: Int,
    val ecoActions: Int,
    val onboarded: Boolean,
    val badges: List<BadgeState>,
) {
    data class BadgeState(val badge: Badge, val unlocked: Boolean)
}

data class ActivityItem(
    val title: String,
    val points: Int,
    val co2Kg: Double,
    val colorTag: String,
    val iconTag: String,
    val timestamp: Long,
)

class StatsRepository(context: Context) {

    private val dao = AppDatabase.get(context).userDao()

    val snapshot: Flow<UserSnapshot> = dao.observeStats().map { e ->
        val actions = e?.ecoActions ?: 0
        UserSnapshot(
            userName = e?.userName ?: "Friend",
            points = e?.points ?: 0,
            co2SavedKg = e?.co2SavedKg ?: 0.0,
            streak = e?.streak ?: 0,
            ecoActions = actions,
            onboarded = e?.onboarded ?: false,
            badges = Badge.entries.map { UserSnapshot.BadgeState(it, it.isUnlocked(actions)) },
        )
    }

    val recentActivity: Flow<List<ActivityItem>> = dao.observeActivity(20).map { rows ->
        rows.map { r ->
            ActivityItem(r.title, r.points, r.co2Kg, r.colorTag, r.iconTag, r.timestamp)
        }
    }

    suspend fun ensureSeeded() {
        if (dao.getStats() == null) {
            dao.upsertStats(UserStatsEntity(id = 0, onboarded = false))
        }
    }

    suspend fun completeOnboarding(name: String) {
        val cur = dao.getStats() ?: UserStatsEntity()
        dao.upsertStats(cur.copy(userName = name.ifBlank { "Friend" }, onboarded = true))
    }

    suspend fun logAction(
        title: String,
        points: Int,
        co2Kg: Double,
        colorTag: String,
        iconTag: String,
    ) {
        val cur = dao.getStats() ?: UserStatsEntity()
        val today = LocalDate.now()
        val newStreak = Streak.apply(cur.streak, cur.lastActionEpochDay, today)

        dao.insertActivity(
            ActivityLogEntity(
                title = title,
                points = points,
                co2Kg = co2Kg,
                colorTag = colorTag,
                iconTag = iconTag,
                timestamp = System.currentTimeMillis(),
            )
        )
        dao.upsertStats(
            cur.copy(
                points = cur.points + points,
                co2SavedKg = cur.co2SavedKg + co2Kg,
                streak = newStreak,
                ecoActions = cur.ecoActions + 1,
                lastActionEpochDay = today.toEpochDay(),
            )
        )
    }

    /**
     * Redeem a reward: deducts [cost] points if the balance covers it.
     * Returns true on success, false when the balance is insufficient.
     */
    suspend fun redeem(cost: Int): Boolean {
        val cur = dao.getStats() ?: return false
        if (cur.points < cost) return false
        dao.upsertStats(cur.copy(points = cur.points - cost))
        return true
    }
}
