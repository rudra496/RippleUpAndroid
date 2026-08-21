package com.yft.rippleup.util

import android.os.Build
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Production guard rails for action logging.
 *
 * The original ytf web demo had zero verification (pure client-side counter).
 * This module adds the production rules:
 *  - per-action-type cooldowns (real-world plausible intervals)
 *  - daily rewarded-action cap
 *  - HMAC-SHA256 integrity tag over persisted stats (tamper detection)
 *
 * HONEST LIMIT: these are client-side controls. A determined attacker with a
 * decompiled APK can bypass them. True anti-cheat requires a server; this
 * raises the bar far above "open the DB and type any number", which was the
 * previous state.
 */
object Guard {

    /** Cooldown per action key, in milliseconds. */
    val COOLDOWN_MS: Map<String, Long> = mapOf(
        "refill" to 45L * 60_000,     // one rewarded refill per 45 min
        "recycle" to 30L * 60_000,
        "food" to 90L * 60_000,
        "transit" to 120L * 60_000,
        "scan-campus-refill" to 60L * 60_000,
        "scan-green-grocer" to 60L * 60_000,
        "scan-recycle-bin" to 60L * 60_000,
    )

    const val DAILY_ACTION_CAP = 12

    /** Points that can be earned from actions in a single calendar day. */
    const val DAILY_POINTS_CAP = 300

    sealed class Verdict {
        data object Allowed : Verdict()
        data class Cooldown(val remainingMs: Long) : Verdict()
        data object DailyActionCap : Verdict()
        data class DailyPointsCap(val earnedToday: Int) : Verdict()
    }

    fun check(
        actionKey: String,
        lastActionAtMs: Long,
        actionsToday: Int,
        pointsToday: Int,
        nowMs: Long = System.currentTimeMillis(),
    ): Verdict {
        if (actionsToday >= DAILY_ACTION_CAP) return Verdict.DailyActionCap
        if (pointsToday >= DAILY_POINTS_CAP) return Verdict.DailyPointsCap(pointsToday)
        val cooldown = COOLDOWN_MS[actionKey] ?: return Verdict.Allowed
        val elapsed = nowMs - lastActionAtMs
        return if (lastActionAtMs <= 0 || elapsed >= cooldown) Verdict.Allowed
        else Verdict.Cooldown(cooldown - elapsed)
    }

    fun formatRemaining(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        return when {
            h > 0 -> "${h}h ${m}m"
            m > 0 -> "${m}m ${s}s"
            else -> "${s}s"
        }
    }

    // --- Tamper-evident storage -------------------------------------------------

    /**
     * Derives an obfuscated device key. R8 minification (release) strips the
     * obvious string; this is best-effort client-side tamper detection.
     */
    private val secret: ByteArray by lazy {
        val seed = "ripplup::" + Build.FINGERPRINT.reversed() + "::" + longSeed()
        seed.toByteArray()
    }

    private fun longSeed(): String = java.lang.Long.toString(0x524950504C5550L, 36) // "ripplup" magic

    fun tag(vararg fields: Any?): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret, "HmacSHA256"))
        val payload = fields.joinToString("|") { it.toString() }
        val bytes = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }.take(40)
    }

    fun verify(tag: String?, vararg fields: Any?): Boolean =
        !tag.isNullOrBlank() && tag == Guard.tag(*fields)
}
