package com.yft.rippleup.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector

/** Bottom-nav destinations. Onboarding is a separate, one-time route. */
enum class TopDest(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home("home", "Home", Icons.Outlined.Home),
    Actions("actions", "Log", Icons.Outlined.AddCircle),
    Scan("scan", "Scan", Icons.Outlined.QrCodeScanner),
    Leaderboard("leaderboard", "Ranks", Icons.Outlined.Leaderboard),
    More("more", "More", Icons.Outlined.Explore),
}

object Routes {
    const val Onboarding = "onboarding"
    val startRoute = TopDest.Home.route
}
