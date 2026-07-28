package com.yft.rippleup.ui.screens.leaderboard

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.components.SectionHeader
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.Stroke
import com.yft.rippleup.util.CalcInputs
import com.yft.rippleup.util.EcoMath
import com.yft.rippleup.util.clickableNoRipple

/** Community standings: Teams / Individuals / Impact Calculator tabs. */
@Composable
fun LeaderboardScreen() {
    var tab by remember { mutableStateOf(0) }
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader(
            title = "Community Standings",
            subtitle = "See which youth groups and individuals lead in verified sustainable actions.",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabChip("Teams", tab == 0) { tab = 0 }
            TabChip("Individuals", tab == 1) { tab = 1 }
            TabChip("Calculator", tab == 2, icon = Icons.Outlined.Calculate) { tab = 2 }
        }

        when (tab) {
            0 -> TeamsView()
            1 -> IndividualsView()
            2 -> CalculatorView()
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(label)
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Emerald,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

@Composable
private fun TeamsView() {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20, contentPadding = PaddingValues(8.dp)) {
        Column {
            HeaderRow()
            LeaderboardData.teams.forEach { row ->
                RankingRow(
                    rank = "${row.rank}",
                    name = row.name,
                    detail = "${row.actions} actions · ${row.co2Kg} kg",
                    points = "${row.points} pts",
                    highlight = row.isMine,
                    tag = if (row.isMine) "My Team" else null,
                    linkedin = null,
                )
            }
        }
    }
}

@Composable
private fun IndividualsView() {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20, contentPadding = PaddingValues(8.dp)) {
        Column {
            HeaderRow()
            LeaderboardData.individuals.forEach { row ->
                RankingRow(
                    rank = "${row.rank}",
                    name = row.name,
                    detail = "${row.actions} actions · ${row.co2Kg} kg",
                    points = "${row.points} pts",
                    highlight = row.isMine,
                    tag = row.role,
                    linkedin = row.linkedin,
                )
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text("#", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Name", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Impact", modifier = Modifier.weight(1.6f), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Points", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RankingRow(
    rank: String,
    name: String,
    detail: String,
    points: String,
    highlight: Boolean,
    tag: String?,
    linkedin: String?,
) {
    val ctx = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (highlight) Emerald.copy(alpha = 0.12f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(rank, modifier = Modifier.weight(0.4f),
            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            color = if (rank == "1") Gold else MaterialTheme.colorScheme.onBackground)
        Column(modifier = Modifier.weight(2f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                if (linkedin != null) {
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Outlined.OpenInNew, contentDescription = "LinkedIn",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp).clip(RoundedCornerShape(2.dp))
                            .clickableNoRipple { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkedin))) })
                }
            }
            if (tag != null) {
                Text(tag, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        Text(detail, modifier = Modifier.weight(1.6f), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(points, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold, color = Emerald)
    }
}

// --- Impact Calculator -------------------------------------------------------

@Composable
private fun CalculatorView() {
    var refills by remember { mutableStateOf(5f) }
    var recycles by remember { mutableStateOf(3f) }
    var commutes by remember { mutableStateOf(10f) }
    var foods by remember { mutableStateOf(2f) }

    val result = remember(refills, recycles, commutes, foods) {
        EcoMath.compute(
            CalcInputs(
                refills = refills.toInt(),
                recycles = recycles.toInt(),
                commutesKm = commutes.toInt(),
                foods = foods.toInt(),
            )
        )
    }

    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column {
            Text("Estimate Your Sustainability Impact", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text("Adjust your weekly habits and see your projected impact.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            SliderRow("💧 Reusable Bottle Refills", refills, 0f..30f) { refills = it }
            SliderRow("♻️ Plastic Bottles Recycled", recycles, 0f..20f) { recycles = it }
            SliderRow("🚲 Green Commuting (km)", commutes, 0f..50f) { commutes = it }
            SliderRow("🍎 Food Waste Avoided", foods, 0f..14f) { foods = it }

            Spacer(Modifier.height(18.dp))

            // Gauge
            Gauge(progress = result.gaugeProgress, tier = result.tier)

            Spacer(Modifier.height(14.dp))

            // Result grid
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ResultCard("${"%.1f".format(result.co2PerWeek)}", "kg CO₂ / wk", Modifier.weight(1f))
                ResultCard("${result.plasticPerWeek}", "Plastic / wk", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ResultCard("${"%.1f".format(result.wastePerWeek)}", "kg Food / wk", Modifier.weight(1f))
                ResultCard("${result.points}", "Est. Points", Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "🌿 Annual Impact: You would offset approximately ${"%.1f".format(result.annualCo2Kg)} kg of CO₂ and save ${result.annualPlastic} plastic bottles from landfills each year!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SliderRow(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()}", style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold, color = Emerald)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range)
    }
}

@Composable
private fun ResultCard(value: String, label: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Emerald)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
