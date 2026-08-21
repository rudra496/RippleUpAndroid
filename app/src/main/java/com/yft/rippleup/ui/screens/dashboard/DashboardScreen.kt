package com.yft.rippleup.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.yft.rippleup.data.repo.ActivityItem
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.ActivityRow
import com.yft.rippleup.ui.components.ConfettiOverlay
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.theme.BgSurface
import com.yft.rippleup.ui.theme.CardDark
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.LimeGreen
import com.yft.rippleup.ui.theme.Orange
import com.yft.rippleup.ui.theme.Teal
import com.yft.rippleup.ui.theme.TealLight
import com.yft.rippleup.ui.theme.TealSoft
import java.util.Calendar
import kotlin.math.min

/**
 * The Figma home: personalised greeting + avatar, a hero points card with
 * circular progress and leaf flourishes, a 2x2 quick-action grid, streak stats,
 * and the recent-activity feed.
 */
@Composable
fun DashboardScreen(
    vm: StatsViewModel,
    onOpenActions: () -> Unit = {},
    onOpenScan: () -> Unit = {},
    onOpenRewards: () -> Unit = {},
    onOpenLeaderboard: () -> Unit = {},
) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GreetingBar(userName = state?.userName ?: "Friend")

            PointsHeroCard(
                points = state?.points ?: 0,
                co2Kg = state?.co2SavedKg ?: 0.0,
                streak = state?.streak ?: 0,
                ecoActions = state?.ecoActions ?: 0,
            )

            QuickActions(
                onLogAction = onOpenActions,
                onScan = onOpenScan,
                onRewards = onOpenRewards,
                onLeaderboard = onOpenLeaderboard,
            )

            RecentActionsSection(activity)
            Spacer(Modifier.height(8.dp))
        }

        ConfettiOverlay(burstKey = pulseTick)
    }
}

@Composable
private fun GreetingBar(userName: String) {
    val hour = rememberHour()
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "$greeting,",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                userName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(Teal.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                userName.trim().take(1).uppercase().ifEmpty { "R" },
                color = TealSoft,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier.size(40.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun rememberHour(): Int {
    return androidx.compose.runtime.remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
}

/**
 * Hero card: points balance with a circular progress ring (toward the next
 * badge), CO2 + streak pills, and subtle leaf flourishes — per the Figma.
 */
@Composable
private fun PointsHeroCard(points: Int, co2Kg: Double, streak: Int, ecoActions: Int) {
    // Progress toward Gold badge (16 actions) — echoes the web's badge ladder
    val progress = (ecoActions / 16f).coerceIn(0f, 1f)
    val animated by androidx.compose.animation.core.animateFloatAsState(
        targetValue = progress, androidx.compose.animation.core.tween(900), label = "ring",
    )

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 26,
        glow = true,
        contentPadding = PaddingValues(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(Modifier.size(196.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(196.dp)) {
                        val stroke = 13.dp.toPx()
                        val dia = min(size.width, size.height) - stroke
                        val tl = Offset((size.width - dia) / 2f, (size.height - dia) / 2f)
                        drawArc(
                            color = Color.White.copy(alpha = 0.08f),
                            startAngle = -90f, sweepAngle = 360f, useCenter = false,
                            topLeft = tl, size = Size(dia, dia),
                            style = Stroke(stroke, cap = StrokeCap.Round),
                        )
                        drawArc(
                            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                                listOf(Teal, TealLight, TealSoft, Teal)
                            ),
                            startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                            topLeft = tl, size = Size(dia, dia),
                            style = Stroke(stroke, cap = StrokeCap.Round),
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$points",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            "RipplUp Points",
                            style = MaterialTheme.typography.labelLarge,
                            color = TealSoft,
                        )
                        Text(
                            "${(16 - ecoActions).coerceAtLeast(0)} actions to Gold",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                // Leaf flourishes at ring edges (Figma detail)
                MiniLeaf(Modifier.offset(x = (-8).dp, y = 6.dp).size(22.dp), TealLight)
                MiniLeaf(Modifier.offset(x = 170.dp, y = 150.dp).size(18.dp), Teal.copy(alpha = 0.7f))
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatPill(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Outlined.LocalFireDepartment, null, tint = Orange, modifier = Modifier.size(16.dp)) },
                    value = "$streak",
                    label = "day streak",
                    tint = Orange,
                )
                StatPill(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Text("CO₂", style = MaterialTheme.typography.labelSmall, color = LimeGreen)
                    },
                    value = String.format("%.1f", co2Kg),
                    label = "kg saved",
                    tint = LimeGreen,
                )
                StatPill(
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Outlined.EmojiEvents, null, tint = Gold, modifier = Modifier.size(16.dp)) },
                    value = "$ecoActions",
                    label = "actions",
                    tint = Gold,
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    modifier: Modifier,
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    tint: Color,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun MiniLeaf(modifier: Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w / 2f, 0f)
            quadraticTo(w, h * 0.4f, w / 2f, h)
            quadraticTo(0f, h * 0.4f, w / 2f, 0f)
        }
        drawPath(path, color = color)
    }
}

/** The 2x2 quick-action grid from the Figma home. */
@Composable
private fun QuickActions(
    onLogAction: () -> Unit,
    onScan: () -> Unit,
    onRewards: () -> Unit,
    onLeaderboard: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            QuickCard("Log Action", "+20 pts", Icons.Outlined.AddTask, Teal, Modifier.weight(1f), onLogAction)
            QuickCard("Scan QR", "verify", Icons.Outlined.QrCodeScanner, TealLight, Modifier.weight(1f), onScan)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            QuickCard("Rewards", "redeem", Icons.Outlined.Redeem, Gold, Modifier.weight(1f), onRewards)
            QuickCard("Leaderboard", "ranks", Icons.Outlined.EmojiEvents, Color(0xFF9B7EF0), Modifier.weight(1f), onLeaderboard)
        }
    }
}

@Composable
private fun QuickCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Box(
            Modifier.size(42.dp).clip(CircleShape).background(tint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.height(10.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RecentActionsSection(activity: List<ActivityItem>) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column {
            Text("Recent Verified Actions", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            if (activity.isEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "No actions yet. Tap Log Action to record your first sustainable action and watch the confetti fly! 🍃",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
            } else {
                activity.take(6).forEach { item ->
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
