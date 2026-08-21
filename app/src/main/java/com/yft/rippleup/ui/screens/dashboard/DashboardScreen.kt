package com.yft.rippleup.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Create
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.data.repo.ActivityItem
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.ConfettiOverlay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Exact Figma colors (RipplUp UN MVP, Home page)
private val Bg = Color(0xFFF5F6F6)
private val Card = Color(0xFFFFFFFF)
private val Teal = Color(0xFF0D9488)
private val Ink = Color(0xFF0C2620)
private val Muted = Color(0xFF5A8A82)
private val SoftTeal = Color(0xFF479790)
private val Gold = Color(0xFFF9D14C)
private val TealGlow = Color(0xFF8FFBE6)
private val GrayCircle = Color(0xFFD6D6D6)
private val TagBg = Color(0xFFE8E3DE)
private val TagInk = Color(0xFF666666)
private val Orange = Color(0xFFF07021)
private val OrangeInk = Color(0xFFFFFDF7)
private val ItemInk = Color(0xFF373737)

/**
 * HOME — exact rebuild from the Figma API spec:
 * weekly habit calendar strip → greeting → gradient streak hero →
 * Today's Ripples timeline list → Upcoming Events.
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

    Box(Modifier.fillMaxSize().background(Bg)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            WeeklyCalendar(streak = state?.streak ?: 0, actions = state?.ecoActions ?: 0)

            Text(
                "Hey ${state?.userName ?: "there"}!",
                fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink,
            )

            StreakHero(
                days = state?.streak ?: 0,
                co2Grams = ((state?.co2SavedKg ?: 0.0) * 1000).toInt(),
                points = state?.points ?: 0,
                totalActions = state?.ecoActions ?: 0,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Today’s Ripples list", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink,
                    modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.Create, contentDescription = "Edit", tint = ItemInk, modifier = Modifier.size(20.dp))
            }

            TodayList(activity)

            UpcomingEvents()

            Spacer(Modifier.height(8.dp))
        }
        ConfettiOverlay(burstKey = pulseTick)
    }
}

/** Mon..Sun cards: gold/teal checks for done days, gray for pending. */
@Composable
private fun WeeklyCalendar(streak: Int, actions: Int) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val todayIdx = (LocalDate.now().dayOfWeek.value + 5) % 7 // Mon=0
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        days.forEachIndexed { i, day ->
            val done = i < todayIdx && actions > 0 && streak > 0 && i < (streak.coerceAtMost(7))
            val isToday = i == todayIdx && actions > 0 && streak > 0
            val circleColor = when {
                isToday -> TealGlow
                done -> Gold
                else -> GrayCircle
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Card)
                    .border(1.dp, Color(0x14000000), RoundedCornerShape(8.dp))
                    .padding(horizontal = 9.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(day, fontSize = 12.sp, color = Color(0xFF000000))
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier.size(22.dp).clip(CircleShape).background(circleColor),
                    contentAlignment = Alignment.Center,
                ) {
                    if (done || isToday) {
                        Text("✓", color = if (isToday) Teal else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/** Gradient teal hero: streak circle + copy + pills. Exact: 364x120 r=24. */
@Composable
private fun StreakHero(days: Int, co2Grams: Int, points: Int, totalActions: Int) {
    Box(
        Modifier.fillMaxWidth().height(120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF14B8A6), Teal))),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(84.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF508E89), Teal))),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$days", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ItemInk)
                    Text("Days", fontSize = 12.sp, color = Color(0xFF96BAB0))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Your Ripples have been adding up!",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = ItemInk)
                Text(
                    "You've helped avoid an estimated ${if (co2Grams >= 1000) "%.1f".format(co2Grams / 1000.0) + " kg" else "$co2Grams g"} CO₂e this week.",
                    fontSize = 12.sp, color = Muted,
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HeroPill("Longest streak", bg = Card, text = SoftTeal)
                    HeroPill("$days Days", bg = SoftTeal, text = Color(0xFFADE6D5))
                    HeroPill("$points+ pts", bg = SoftTeal, text = Color(0xFFADE6D5))
                }
            }
        }
    }
}

@Composable
private fun HeroPill(label: String, bg: Color, text: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(8.dp)).background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(label, fontSize = 10.sp, color = text)
    }
}

/** White timeline card with date chip + ripple items + self/QR report tags. */
@Composable
private fun TodayList(activity: List<ActivityItem>) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Card)
            .border(1.dp, Color(0x14000000), RoundedCornerShape(24.dp)).padding(14.dp),
    ) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
        Text(today, fontSize = 12.sp, color = Muted,
            modifier = Modifier.align(Alignment.CenterHorizontally))

        if (activity.isEmpty()) {
            Spacer(Modifier.height(18.dp))
            Text("No ripples yet today.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("Log your first sustainable action to start your ripple!", fontSize = 12.sp, color = Muted)
            Spacer(Modifier.height(18.dp))
        } else {
            activity.take(3).forEachIndexed { idx, item ->
                Spacer(Modifier.height(10.dp))
                RippleRow(item)
            }
        }
    }
}

@Composable
private fun RippleRow(item: ActivityItem) {
    Row(Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(23.dp).clip(CircleShape).background(Teal)
                    .border(2.dp, Card, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            if (true) {
                Spacer(Modifier.height(4.dp))
                Box(Modifier.width(1.dp).height(30.dp).background(Color(0x1F0D9488)))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF000000))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Tag(item.tagLabel(), bg = TagBg, text = TagInk)
                Tag("+${item.points} pts",
                    bg = if (item.colorTag == "green") Orange else Color(0xFF8F8F8F),
                    text = OrangeInk)
            }
        }
    }
}

private fun ActivityItem.tagLabel(): String = if (iconTag.contains("Qr", true)) "QR Reported" else "Self Reported"

@Composable
private fun Tag(label: String, bg: Color, text: Color) {
    Box(Modifier.clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(label, fontSize = 10.sp, color = text)
    }
}

/** Upcoming Events: header + See all + horizontal white r=16 cards. */
@Composable
private fun UpcomingEvents() {
    val events = listOf(
        "Community Cleanup" to "Sat, Sept 21 · 9:00 AM",
        "Eco Workshop: Repair Café" to "Sun, Sept 29 · 2:00 PM",
    )
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Upcoming Events", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink,
                modifier = Modifier.weight(1f))
            Text("See all", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Teal)
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Teal, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            events.forEach { (title, date) ->
                Column(
                    Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(Card)
                        .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                ) {
                    Box(Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE8F7F4)))
                    Spacer(Modifier.height(8.dp))
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(date, fontSize = 10.sp, color = Muted)
                }
            }
        }
    }
}
