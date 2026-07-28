package com.yft.rippleup.data.model

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.Orange

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
    Bronze("Bronze", 0, Orange, "WorkspacePremium"),
    Silver("Silver", 14, Color(0xFFB8C0CC), "MilitaryTech"),
    Gold("Gold", 16, Gold, "EmojiEvents");

    /** Whether this badge is unlocked given the user's current action count. */
    fun isUnlocked(actionCount: Int): Boolean = actionCount >= actionsRequired

    /** Next badge to chase, or null if Gold is reached. */
    fun next(): Badge? = entries.firstOrNull { it.actionsRequired > actionsRequired }
}
