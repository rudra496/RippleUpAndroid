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
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

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

        // --- GitHub cloud sync ---
        GitHubSyncCard(vm)

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

// --- GitHub Cloud Sync ----------------------------------------------------------

/**
 * Links the user's own GitHub account for cloud backup: they paste a personal
 * token (gist scope), which is stored encrypted; stats sync to a secret gist
 * in their own GitHub. No server, no shared secrets — GitHub handles it all.
 */
@Composable
private fun GitHubSyncCard(vm: com.yft.rippleup.ui.StatsViewModel) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val snackbar = androidx.compose.material3.SnackbarHostState()
    var tokenInput by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    val linked = vm.gitHubSync.hasToken
    val lastSync = vm.gitHubSync.lastSync

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    androidx.compose.material.icons.Icons.Outlined.Cloud,
                    contentDescription = null, tint = Teal, modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text("GitHub Cloud Sync", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                if (linked) "Linked — your progress backs up to a secret gist in your own GitHub account."
                else "Link your GitHub account to back up progress to your own GitHub (secret gist). Works across devices.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
            if (!linked) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "1) Create a token at github.com/settings/tokens (classic, 'gist' scope only)\n2) Paste it below",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ghp_… or github_pat_…", style = MaterialTheme.typography.bodySmall) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedContainerColor = com.yft.rippleup.ui.theme.FieldBg,
                        focusedContainerColor = com.yft.rippleup.ui.theme.FieldBg,
                    ),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!linked) {
                    SyncAction("Link & Save", enabled = tokenInput.isNotBlank()) {
                        scope.launch {
                            val res = vm.gitHubSync.validateToken(tokenInput)
                            res.onSuccess { vm.gitHubSync.token = tokenInput }
                            snackbar.showSnackbar(
                                res.fold({ "Linked as @$it ✓" }, { it.message ?: "failed" })
                            )
                        }
                    }
                } else {
                    SyncAction("Backup now") {
                        vm.backupToGithub { res ->
                            scope.launch {
                                snackbar.showSnackbar(res.fold({ "Backed up to GitHub ✓" }, { it.message ?: "failed" }))
                            }
                        }
                    }
                    SyncAction("Restore") {
                        vm.restoreFromGithub { res ->
                            scope.launch {
                                snackbar.showSnackbar(res.fold({ { "Restored from GitHub ✓" }() }, { it.message ?: "failed" }))
                            }
                        }
                    }
                    SyncAction("Unlink", danger = true) {
                        vm.gitHubSync.clearToken()
                        scope.launch { snackbar.showSnackbar("Unlinked") }
                    }
                }
            }
            if (linked && lastSync > 0) {
                Text(
                    "Last sync: " + java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
                        .format(java.util.Date(lastSync)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
    androidx.compose.material3.SnackbarHost(
        hostState = snackbar,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun SyncAction(label: String, enabled: Boolean = true, danger: Boolean = false, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                danger -> com.yft.rippleup.ui.theme.Coral.copy(alpha = 0.15f)
                enabled -> Teal
                else -> CardDark
            },
        ),
        onClick = { if (enabled) onClick() },
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = when {
                danger -> com.yft.rippleup.ui.theme.Coral
                enabled -> Color(0xFF04241E)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}

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
