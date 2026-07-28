package com.yft.rippleup.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.data.model.Badge
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.ActivityRow
import com.yft.rippleup.ui.components.AnimatedCounter
import com.yft.rippleup.ui.components.BadgeChip
import com.yft.rippleup.ui.components.ConfettiOverlay
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.EmeraldDark
import com.yft.rippleup.ui.theme.EmeraldLight
import com.yft.rippleup.ui.theme.Orange

/**
 * The home dashboard — a faithful, real-data version of the phone mockup from
 * the web simulator. Shows the points balance card, streak + eco-actions mini
 * grid, badge row, and the recent-activity feed.
 */
@Composable
fun DashboardScreen(vm: StatsViewModel) {
    val snapshot by vm.snapshot.collectAsState()
    val activity by vm.recentActivity.collectAsState()
    val pulseTick by vm.pulseTick.collectAsState()

    val state = snapshot
    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            TopBar(userName = state?.userName ?: "Friend")

            PointsCard(
                points = state?.points ?: 0,
                co2Kg = state?.co2SavedKg ?: 0.0,
            )

            MiniGrid(
                streak = state?.streak ?: 0,
                ecoActions = state?.ecoActions ?: 0,
            )

            BadgeRow(state?.badges?.map { it.badge to it.unlocked } ?: emptyList())

            RecentActionsSection(activity)
            Spacer(Modifier.height(8.dp))
        }

        // Confetti celebration layer — fires whenever an action is logged.
        ConfettiOverlay(burstKey = pulseTick)
    }
}

@Composable
private fun TopBar(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(CircleShape)
                    .background(Emerald.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Eco, contentDescription = null, tint = Emerald)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Welcome back,", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(userName, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
        }
        Box(
            Modifier.size(40.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PointsCard(points: Int, co2Kg: Double) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        glow = true,
        cornerRadius = 28,
        contentPadding = PaddingValues(24.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Points Balance", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            AnimatedCounter(target = points)
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(EmeraldDark.copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Spa, contentDescription = null,
                    tint = EmeraldLight, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                AnimatedCounter(
                    target = co2Kg,
                    decimals = 1,
                    durationMs = 900,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(" kg CO₂ Saved", style = MaterialTheme.typography.labelLarge,
                    color = EmeraldLight)
            }
        }
    }
}

@Composable
private fun MiniGrid(streak: Int, ecoActions: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MiniCard(
            modifier = Modifier.weight(1f),
            iconTint = Orange,
            iconName = Icons.Outlined.LocalFireDepartment,
            value = "$streak",
            label = "Day Streak",
        )
        MiniCard(
            modifier = Modifier.weight(1f),
            iconTint = EmeraldLight,
            iconName = Icons.Outlined.Eco,
            value = "$ecoActions",
            label = "Eco Actions",
        )
    }
}

@Composable
private fun MiniCard(
    modifier: Modifier,
    iconTint: androidx.compose.ui.graphics.Color,
    iconName: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
) {
    GlassPanel(
        modifier = modifier,
        cornerRadius = 22,
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(iconName, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground)
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun BadgeRow(badges: List<Pair<Badge, Boolean>>) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Unlocked Badges", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                badges.forEach { (badge, unlocked) ->
                    BadgeChip(badge = badge, unlocked = unlocked)
                }
            }
        }
    }
}

@Composable
private fun RecentActionsSection(activity: List<com.yft.rippleup.data.repo.ActivityItem>) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column {
            Text("Recent Verified Actions", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(6.dp))
            if (activity.isEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    "No actions yet. Tap Log to record your first sustainable action and watch the confetti fly! 🍃",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(20.dp))
            } else {
                activity.take(8).forEach { item ->
                    ActivityRow(
                        title = item.title,
                        points = item.points,
                        colorTag = item.colorTag,
                        iconTag = item.iconTag,
                        timestamp = item.timestamp,
                    )
                }
            }
        }
    }
}
