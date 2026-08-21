package com.yft.rippleup.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.screens.actions.ActionsScreen
import com.yft.rippleup.ui.screens.dashboard.DashboardScreen
import com.yft.rippleup.ui.screens.leaderboard.LeaderboardScreen
import com.yft.rippleup.ui.screens.more.DiscoverScreen
import com.yft.rippleup.ui.screens.more.EventsScreen
import com.yft.rippleup.ui.screens.more.NotificationsScreen
import com.yft.rippleup.ui.screens.onboarding.OnboardingScreen
import com.yft.rippleup.ui.screens.profile.ProfileScreen
import com.yft.rippleup.ui.screens.rewards.RewardsScreen
import com.yft.rippleup.ui.screens.scan.ScanScreen
import com.yft.rippleup.ui.theme.BgSurface
import com.yft.rippleup.ui.theme.Teal

/**
 * Root composable. Shows onboarding until the user completes it, then the main
 * bottom-nav scaffold (Figma IA: Home / Ranks / Scan / Rewards / Profile).
 */
@Composable
fun RippleUpApp() {
    val vm: StatsViewModel = viewModel()
    val snapshot by vm.snapshot.collectAsState()
    val navController = rememberNavController()

    val current = snapshot
    if (current == null) {
        // First-frame loading (seed row being created).
        Box(Modifier.fillMaxSize().background(BgSurface))
        return
    }

    if (!current.onboarded) {
        OnboardingScreen(vm = vm, onDone = { name -> vm.completeOnboarding(name) })
        return
    }

    AppScaffold(vm, navController)
}

@Composable
private fun AppScaffold(vm: StatsViewModel, navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.startRoute

    val go: (String) -> Unit = { route ->
        if (route != currentRoute) {
            val isTab = TopDest.entries.any { it.route == route }
            navController.navigate(route) {
                if (isTab) {
                    popUpTo(Routes.startRoute) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F6F6),
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TopDest.entries.forEach { dest ->
                    val selected = currentRoute == dest.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { go(dest.route) },
                        icon = {
                            Icon(
                                dest.icon,
                                contentDescription = dest.label,
                                modifier = if (dest == TopDest.Scan) Modifier.size(28.dp) else Modifier.size(22.dp),
                            )
                        },
                        label = { Text(dest.label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Teal,
                            selectedTextColor = Teal,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Teal.copy(alpha = 0.16f),
                        ),
                    )
                }
            }
        },
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = Routes.startRoute,
            modifier = Modifier.padding(inner),
        ) {
            composable(TopDest.Home.route) {
                DashboardScreen(
                    vm = vm,
                    onOpenActions = { go(Routes.Actions) },
                    onOpenScan = { go(TopDest.Scan.route) },
                    onOpenRewards = { go(TopDest.Rewards.route) },
                    onOpenLeaderboard = { go(Routes.Events) },
                )
            }
            composable(TopDest.Discover.route) { DiscoverScreen() }
            composable(TopDest.Scan.route) { ScanScreen(vm) }
            composable(TopDest.Rewards.route) { RewardsScreen(vm) }
            composable(TopDest.Profile.route) { ProfileScreen(vm) }
            composable(Routes.Actions) { ActionsScreen(vm) }
            composable(Routes.Notifications) { NotificationsScreen() }
            composable(Routes.Events) { EventsScreen() }
        }
    }
}
