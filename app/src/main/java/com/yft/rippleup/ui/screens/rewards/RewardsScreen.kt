package com.yft.rippleup.ui.screens.rewards

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.ui.StatsViewModel

// Exact Figma colors
private val Bg = Color(0xFFF5F6F6)
private val Card = Color(0xFFFFFFFF)
private val Teal = Color(0xFF0D9488)
private val Ink = Color(0xFF0C2620)
private val Muted = Color(0xFF5A8A82)

private data class Milestone(
    val emoji: String, val name: String, val requirement: String,
    val done: Boolean, val remainingPts: Int? = null,
)

/**
 * REWARDS — exact rebuild per Figma: teal "Rewards" header (36w white),
 * subtitle, then milestone badge cards (emoji + name + requirement +
 * points-remaining progress). Milestones computed from real stats.
 */
@Composable
fun RewardsScreen(vm: StatsViewModel) {
    val snapshot by vm.snapshot.collectAsState()
    val s = snapshot
    val points = s?.points ?: 0
    val actions = s?.ecoActions ?: 0

    val milestones = listOf(
        Milestone("🌱", "Ripple Starter", "Complete your first action", actions >= 1),
        Milestone("⭐", "First Refill", "Refill your bottle for the first time", actions >= 1),
        Milestone("🌊", "Plastic Saver", "Avoid 10 plastic bottles", actions >= 10, (10 - actions).coerceAtLeast(0) * 20),
        Milestone("♻️", "Zero Waste Hero", "Complete 5 recycling actions", actions >= 5, (5 - actions).coerceAtLeast(0) * 30),
        Milestone("🏆", "Community Champion", "Attend 3 community events", false, 200),
        Milestone("🌍", "Ripple Ambassador", "Reach 5,000 total points", points >= 5000, (5000 - points).coerceAtLeast(0)),
    )

    Column(Modifier.fillMaxSize().background(Bg)) {
        // Teal header
        Column(
            Modifier.fillMaxWidth().background(Teal).padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text("Rewards", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Turn every ripple into a milestone worth celebrating!",
                fontSize = 12.sp, color = Color(0xFFCFEAE5))
        }
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            milestones.forEach { m -> MilestoneCard(m) }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MilestoneCard(m: Milestone) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
            .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(if (m.done) Color(0xFFE8F7F4) else Color(0xFFF5F6F6)),
            contentAlignment = Alignment.Center,
        ) { Text(m.emoji, fontSize = 20.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(m.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text(m.requirement, fontSize = 12.sp, color = Muted)
        }
        if (m.done) {
            Text("✓ Earned", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Teal)
        } else if (m.remainingPts != null) {
            Text("+${m.remainingPts} pts\nremaining", fontSize = 12.sp,
                fontWeight = FontWeight.Bold, color = Muted, textAlign = androidx.compose.ui.text.style.TextAlign.End)
        }
    }
}
