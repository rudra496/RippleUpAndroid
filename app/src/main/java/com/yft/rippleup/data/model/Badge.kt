package com.yft.rippleup.data.model

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.Orange

/**
 * Badge tier colors. Held in a separate object (not in the enum companion) so
 * that the `Gold` enum entry does not shadow the imported `Gold` theme color
 * during enum class initialization — which would otherwise throw an
 * "Enum entry is uninitialized here" compile error.
 */
private object BadgeColors {
    val orange: Color = Orange
    val silver: Color = Color(0xFFB8C0CC)
    val gold: Color = Gold
}

/**
 * Badge tiers. Thresholds are taken directly from checkBadges() in the web app.js:
 *   Bronze -> unlocked from the start
 *   Silver -> unlocked at 14 eco actions
 *   Gold   -> unlocked at 16 eco actions
 */
enum class Badge(
    val label: String,
    val actionsRequired: Int,
    val color: Color,
    val iconName: String,
) {
    Bronze("Bronze", 0, BadgeColors.orange, "WorkspacePremium"),
    Silver("Silver", 14, BadgeColors.silver, "MilitaryTech"),
    Gold("Gold", 16, BadgeColors.gold, "EmojiEvents");

    /** Whether this badge is unlocked given the user's current action count. */
    fun isUnlocked(actionCount: Int): Boolean = actionCount >= actionsRequired

    /** Next badge to chase, or null if Gold is reached. */
    fun next(): Badge? = entries.firstOrNull { it.actionsRequired > actionsRequired }
}
