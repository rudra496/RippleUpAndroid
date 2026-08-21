package com.yft.rippleup.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom-nav destinations per the Figma: Home, Ranks, Scan (center), Rewards,
 * Profile. Onboarding is a separate one-time route; Actions opens from the
 * home quick-action grid.
 */
enum class TopDest(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home("home", "Home", Icons.Outlined.Home),
    Leaderboard("leaderboard", "Ranks", Icons.Outlined.EmojiEvents),
    Scan("scan", "Scan", Icons.Outlined.QrCodeScanner),
    Rewards("rewards", "Rewards", Icons.Outlined.Redeem),
    Profile("profile", "Profile", Icons.Outlined.Person),
}

/** Non-tab routes reachable via navigation callbacks. */
object Routes {
    const val Onboarding = "onboarding"
    const val Actions = "actions"
    val startRoute = TopDest.Home.route
}
