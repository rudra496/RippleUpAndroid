package com.yft.rippleup.ui.screens.rewards

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Cyan
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.LimeGreen
import com.yft.rippleup.ui.theme.Orange
import com.yft.rippleup.ui.theme.Purple
import com.yft.rippleup.ui.theme.Teal
import com.yft.rippleup.ui.theme.TealLight

/**
 * Redeemable rewards catalogue — styled after the Figma rewards screen:
 * vertical cards with icon, name, partner, points cost and a redeem button.
 * Redeeming deducts points from the user's balance (see StatsRepository).
 */
data class Reward(
    val id: String,
    val title: String,
    val partner: String,
    val pointsCost: Int,
    val iconTag: String,
    val tint: Color,
)

object RewardsCatalog {
    val rewards = listOf(
        Reward("coffee", "Free Coffee Refill", "Campus Café Partner", 200, "Coffee", Orange),
        Reward("store", "10% Off Eco Store", "GreenMart", 300, "Storefront", Teal),
        Reward("bus", "Bus Day Pass", "City Transit", 400, "DirectionsBus", Cyan),
        Reward("bottle", "Reusable Bottle", "EcoGoods", 500, "WaterDrop", TealLight),
        Reward("tree", "Plant a Tree Certificate", "OneTree Initiative", 800, "Forest", LimeGreen),
        Reward("tote", "RipplUp Tote Bag", "Youth for Tomorrow", 1000, "ShoppingBag", Gold),
        Reward("workshop", "VIP Eco Workshop", "Learning Planet", 1500, "School", Purple),
    )
}
