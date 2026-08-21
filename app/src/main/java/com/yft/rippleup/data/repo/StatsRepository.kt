package com.yft.rippleup.data.repo

import android.content.Context
import com.yft.rippleup.data.db.ActivityLogEntity
import com.yft.rippleup.data.db.AppDatabase
import com.yft.rippleup.data.db.UserStatsEntity
import com.yft.rippleup.data.model.Badge
import com.yft.rippleup.util.Guard
import com.yft.rippleup.util.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Snapshot of everything the UI needs to render the dashboard. Badges are
 * computed from ecoActions. `integrityOk` surfaces tamper detection.
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

/** Result of a guarded action log attempt. */
sealed class LogResult {
    data class Success(val points: Int, val co2Kg: Double) : LogResult()
    data class Rejected(val reason: String) : LogResult()
}

class StatsRepository(context: Context) {

    private val dao = AppDatabase.get(context).userDao()

    val snapshot: Flow<UserSnapshot> = dao.observeStats()
        .distinctUntilChanged()
        .map { e -> toSnapshot(e ?: freshStats()) }

    val recentActivity: Flow<List<ActivityItem>> = dao.observeActivity(20)
        .distinctUntilChanged()
        .map { rows -> rows.map { ActivityItem(it.title, it.points, it.co2Kg, it.colorTag, it.iconTag, it.timestamp) } }

    // --- helpers ---------------------------------------------------------------

    private fun freshStats() = UserStatsEntity(id = 0, onboarded = false)

    private fun integrityFields(s: UserStatsEntity) = arrayOf(
        s.userName, s.points, s.co2SavedKg, s.streak, s.ecoActions,
        s.lastActionEpochDay, s.lastRefillAt, s.lastRecycleAt, s.lastFoodAt,
        s.lastTransitAt, s.lastScanCampusAt, s.lastScanGrocerAt, s.lastScanBinAt,
        s.dailyEpochDay, s.actionsToday, s.pointsToday,
    )

    private fun withTag(s: UserStatsEntity) = s.copy(integrityTag = Guard.tag(*integrityFields(s)))

    private fun toSnapshot(e: UserStatsEntity): UserSnapshot {
        val ok = Guard.verify(e.integrityTag, *integrityFields(e))
        val actions = if (ok) e.ecoActions else 0
        return UserSnapshot(
            userName = e.userName,
            points = if (ok) e.points else 0,
            co2SavedKg = if (ok) e.co2SavedKg else 0.0,
            streak = if (ok) e.streak else 0,
            ecoActions = actions,
            onboarded = e.onboarded,
            badges = Badge.entries.map { UserSnapshot.BadgeState(it, it.isUnlocked(actions)) },
        )
    }

    private fun lastAtFor(key: String, s: UserStatsEntity): Long = when (key) {
        "refill" -> s.lastRefillAt
        "recycle" -> s.lastRecycleAt
        "food" -> s.lastFoodAt
        "transit" -> s.lastTransitAt
        "scan-campus-refill" -> s.lastScanCampusAt
        "scan-green-grocer" -> s.lastScanGrocerAt
        "scan-recycle-bin" -> s.lastScanBinAt
        else -> 0L
    }

    private fun withLastAt(key: String, s: UserStatsEntity, at: Long): UserStatsEntity = when (key) {
        "refill" -> s.copy(lastRefillAt = at)
        "recycle" -> s.copy(lastRecycleAt = at)
        "food" -> s.copy(lastFoodAt = at)
        "transit" -> s.copy(lastTransitAt = at)
        "scan-campus-refill" -> s.copy(lastScanCampusAt = at)
        "scan-green-grocer" -> s.copy(lastScanGrocerAt = at)
        "scan-recycle-bin" -> s.copy(lastScanBinAt = at)
        else -> s
    }

    /** Pre-check whether an action may be logged right now (for UI states). */
    suspend fun canLog(actionKey: String): Guard.Verdict {
        val s = dao.getStats() ?: freshStats()
        val today = LocalDate.now().toEpochDay()
        val rolled = if (s.dailyEpochDay != today) s.copy(actionsToday = 0, pointsToday = 0, dailyEpochDay = today) else s
        return Guard.check(actionKey, lastAtFor(actionKey, rolled), rolled.actionsToday, rolled.pointsToday)
    }

    // --- core ------------------------------------------------------------------

    suspend fun ensureSeeded() {
        val existing = dao.getStats()
        if (existing == null) {
            dao.upsertStats(withTag(freshStats()))
        } else if (!Guard.verify(existing.integrityTag, *integrityFields(existing))) {
            // Tampered rows reset to a clean slate (name/onboarding preserved).
            dao.upsertStats(withTag(existing.copy(
                points = 0, co2SavedKg = 0.0, streak = 0, ecoActions = 0,
                actionsToday = 0, pointsToday = 0,
            )))
        }
    }

    suspend fun completeOnboarding(name: String) {
        val cur = (dao.getStats() ?: freshStats())
            .let { if (it.dailyEpochDay != LocalDate.now().toEpochDay())
                it.copy(actionsToday = 0, pointsToday = 0, dailyEpochDay = LocalDate.now().toEpochDay()) else it }
        dao.upsertStats(withTag(cur.copy(userName = name.ifBlank { "Friend" }, onboarded = true)))
    }

    /**
     * Guarded action log. Enforces cooldowns + daily caps, keeps the HMAC tag
     * in sync, and writes an immutable audit-log row on success.
     */
    suspend fun logAction(
        actionKey: String,
        title: String,
        points: Int,
        co2Kg: Double,
        colorTag: String,
        iconTag: String,
    ): LogResult {
        var cur = dao.getStats() ?: freshStats()
        if (!Guard.verify(cur.integrityTag, *integrityFields(cur))) return LogResult.Rejected("Storage integrity check failed — stats were reset.")

        val today = LocalDate.now()
        // day rollover
        if (cur.dailyEpochDay != today.toEpochDay()) {
            cur = cur.copy(actionsToday = 0, pointsToday = 0, dailyEpochDay = today.toEpochDay())
        }

        when (val v = Guard.check(actionKey, lastAtFor(actionKey, cur), cur.actionsToday, cur.pointsToday)) {
            is Guard.Verdict.Cooldown -> return LogResult.Rejected(
                "Already logged. Try again in ${Guard.formatRemaining(v.remainingMs)}.")
            Guard.Verdict.DailyActionCap -> return LogResult.Rejected(
                "Daily limit reached (${Guard.DAILY_ACTION_CAP} actions/day). Come back tomorrow!")
            is Guard.Verdict.DailyPointsCap -> return LogResult.Rejected(
                "Daily points cap reached (${v.earnedToday}/${Guard.DAILY_POINTS_CAP} pts). Fresh start tomorrow!")
            Guard.Verdict.Allowed -> Unit
        }

        val now = System.currentTimeMillis()
        val newStreak = Streak.apply(cur.streak, cur.lastActionEpochDay, today)

        dao.insertActivity(
            ActivityLogEntity(
                title = title, points = points, co2Kg = co2Kg,
                colorTag = colorTag, iconTag = iconTag, timestamp = now,
            )
        )
        dao.upsertStats(withTag(withLastAt(actionKey, cur, now).copy(
            points = cur.points + points,
            co2SavedKg = cur.co2SavedKg + co2Kg,
            streak = newStreak,
            ecoActions = cur.ecoActions + 1,
            lastActionEpochDay = today.toEpochDay(),
            actionsToday = cur.actionsToday + 1,
            pointsToday = cur.pointsToday + points,
            dailyEpochDay = today.toEpochDay(),
        )))
        return LogResult.Success(points, co2Kg)
    }

    /** Redeem a reward: deducts [cost] points if the balance covers it. */
    suspend fun redeem(cost: Int): Boolean {
        val cur = dao.getStats() ?: return false
        if (!Guard.verify(cur.integrityTag, *integrityFields(cur))) return false
        if (cur.points < cost) return false
        dao.upsertStats(withTag(cur.copy(points = cur.points - cost)))
        return true
    }

    /** Replace stats from a trusted GitHub backup (re-tags integrity). */
    suspend fun restoreFromBackup(name: String, points: Int, co2: Double, streak: Int, actions: Int): Boolean {
        val cur = dao.getStats() ?: freshStats()
        if (points < 0 || co2 < 0 || streak < 0 || actions < 0) return false
        // Guard rails on restore: cannot exceed plausible caps.
        if (points > 1_000_000 || co2 > 100_000.0) return false
        dao.upsertStats(withTag(cur.copy(
            userName = name.ifBlank { cur.userName },
            points = points, co2SavedKg = co2, streak = streak, ecoActions = actions,
        )))
        return true
    }

    /** Snapshot for cloud backup. */
    suspend fun backupPayload(): Map<String, Any> {
        val s = dao.getStats() ?: freshStats()
        return mapOf(
            "app" to "ripplup", "schema" to 2,
            "name" to s.userName, "points" to s.points,
            "co2SavedKg" to s.co2SavedKg, "streak" to s.streak,
            "ecoActions" to s.ecoActions,
        )
    }
}
