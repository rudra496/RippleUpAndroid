package com.yft.rippleup.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.yft.rippleup.ui.screens.more.MoreScreen
import com.yft.rippleup.ui.screens.onboarding.OnboardingScreen
import com.yft.rippleup.ui.screens.scan.ScanScreen
import com.yft.rippleup.ui.theme.BgDeep

/**
 * Root composable. Shows onboarding until the user completes it, then the main
 * bottom-nav scaffold.
 */
@Composable
fun RippleUpApp() {
    val vm: StatsViewModel = viewModel()
    val snapshot by vm.snapshot.collectAsState()
    val navController = rememberNavController()

    val current = snapshot
    if (current == null) {
        // First-frame loading (seed row being created).
        Box(Modifier.fillMaxSize().background(BgDeep))
        return
    }

    if (!current.onboarded) {
        OnboardingScreen(onDone = { name -> vm.completeOnboarding(name) })
        return
    }

    AppScaffold(vm, navController)
}

@Composable
private fun AppScaffold(vm: StatsViewModel, navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.startRoute

    Scaffold(
        containerColor = BgDeep,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                TopDest.entries.forEach { dest ->
                    val selected = currentRoute == dest.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(dest.route) {
                                    popUpTo(Routes.startRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
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
            composable(TopDest.Home.route) { DashboardScreen(vm) }
            composable(TopDest.Actions.route) { ActionsScreen(vm) }
            composable(TopDest.Scan.route) { ScanScreen(vm) }
            composable(TopDest.Leaderboard.route) { LeaderboardScreen() }
            composable(TopDest.More.route) { MoreScreen() }
        }
    }
}
