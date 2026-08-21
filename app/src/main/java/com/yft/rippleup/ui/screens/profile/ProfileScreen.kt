package com.yft.rippleup.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yft.rippleup.data.model.Badge
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.BadgeChip
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.screens.more.MoreContent
import com.yft.rippleup.ui.theme.CardDark
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.LimeGreen
import com.yft.rippleup.ui.theme.Orange
import com.yft.rippleup.ui.theme.Stroke
import com.yft.rippleup.ui.theme.Teal
import com.yft.rippleup.ui.theme.TealSoft
import com.yft.rippleup.util.EcoTier

/**
 * Profile screen per the Figma: avatar + name + eco title, a stats row, badge
 * grid, and a settings/menu list. The informational hub (journey, research,
 * SDGs, legal) lives below — one scrollable home for everything personal.
 */
@Composable
fun ProfileScreen(vm: StatsViewModel) {
    val snapshot by vm.snapshot.collectAsState()
    val state = snapshot

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // --- Header: avatar + name + title ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.size(92.dp).clip(CircleShape).background(Teal.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    (state?.userName ?: "F").trim().take(1).uppercase().ifEmpty { "R" },
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealSoft,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                state?.userName ?: "Friend",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                ecoTitle(state?.points ?: 0),
                style = MaterialTheme.typography.titleMedium,
                color = Teal,
            )
        }

        // --- Stats row ---
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ProfileStat("${state?.points ?: 0}", "Points", Teal, Modifier.weight(1f))
            ProfileStat("${state?.ecoActions ?: 0}", "Actions", Gold, Modifier.weight(1f))
            ProfileStat(String.format("%.1f", state?.co2SavedKg ?: 0.0), "kg CO₂", LimeGreen, Modifier.weight(1f))
            ProfileStat("${state?.streak ?: 0}", "Streak", Orange, Modifier.weight(1f))
        }

        // --- Badges ---
        GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
            Column {
                Text("Badges", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Badge.entries.forEach { badge ->
                        BadgeChip(badge = badge, unlocked = badge.isUnlocked(state?.ecoActions ?: 0))
                    }
                }
            }
        }

        // --- Menu list ---
        ProfileMenu()

        // --- Info hub (journey, SDGs, research, legal, footer) ---
        MoreContent()

        Spacer(Modifier.height(8.dp))
    }
}

private fun ecoTitle(points: Int): String = EcoTier.forPoints(points).title

@Composable
private fun ProfileStat(value: String, label: String, tint: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = tint)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private data class MenuItem(val icon: ImageVector, val title: String, val subtitle: String)

@Composable
private fun ProfileMenu() {
    val items = listOf(
        MenuItem(Icons.AutoMirrored.Outlined.HelpOutline, "Help & Support", "FAQs and contact"),
        MenuItem(Icons.Outlined.PrivacyTip, "Privacy", "Your data stays on-device"),
        MenuItem(Icons.Outlined.Eco, "About RipplUp", "Mission & team"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(item.icon, contentDescription = null, tint = Teal, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Text(item.subtitle, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
