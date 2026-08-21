package com.yft.rippleup.ui.screens.profile

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
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.screens.more.MoreContent
import com.yft.rippleup.util.EcoTier
import com.yft.rippleup.util.clickableNoInd
import kotlinx.coroutines.launch

private val Bg = Color(0xFFF5F6F6)
private val Card = Color(0xFFFFFFFF)
private val Teal = Color(0xFF0D9488)
private val Ink = Color(0xFF0C2620)
private val Muted = Color(0xFF5A8A82)
private val Gray = Color(0xFF8F8F8F)
private val MenuInk = Color(0xFF222222)

/** PROFILE — exact per Figma: avatar+name+title, RP/CO2e/Streak stats, settings menu, GitHub backup, info hub. */
@Composable
fun ProfileScreen(vm: StatsViewModel) {
    val snapshot by vm.snapshot.collectAsState()
    val s = snapshot
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var tokenInput by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(Bg)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFE8F7F4)), contentAlignment = Alignment.Center) {
                    Text((s?.userName ?: "F").take(1), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Teal)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(s?.userName ?: "Friend", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(EcoTier.forPoints(s?.points ?: 0).title, fontSize = 14.sp, color = Muted)
                }
            }

            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
                    .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)).padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatBlock("${s?.points ?: 0}", "RP", "Ripple Points")
                StatBlock(
                    if ((s?.co2SavedKg ?: 0.0) >= 1.0) String.format("%.1f kg", s?.co2SavedKg ?: 0.0)
                    else "${((s?.co2SavedKg ?: 0.0) * 1000).toInt()} g",
                    "CO₂e", "CO2e Saved", note = "Estimated")
                StatBlock("${s?.streak ?: 0}", "Days", "Current Streak")
            }

            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
                .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp))) {
                MenuRow("Privacy and Data"); MenuRow("Notifications")
                MenuRow("About Ripple Up"); MenuRow("Help & Support")
            }

            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
                .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)).padding(14.dp)) {
                Text("Cloud Backup (GitHub)", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MenuInk)
                Text(
                    if (vm.gitHubSync.hasToken) "Linked as @${vm.gitHubSync.githubLogin()} — progress backs up to your own GitHub."
                    else "Optional: link a GitHub token (gist scope) to back up progress.",
                    fontSize = 12.sp, color = Muted, modifier = Modifier.padding(top = 4.dp))
                if (!vm.gitHubSync.hasToken) {
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = tokenInput, onValueChange = { tokenInput = it },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        placeholder = { Text("ghp_… (gist scope)", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    SmallBtn("Link", teal = true, enabled = tokenInput.isNotBlank()) {
                        vm.linkGitHub(tokenInput) { res ->
                            scope.launch { snackbar.showSnackbar(res.fold({ "Linked as @$it ✓" }, { it.message ?: "failed" })) }
                        }
                    }
                } else {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallBtn("Backup now", teal = true) {
                            vm.backupToGithub { res -> scope.launch { snackbar.showSnackbar(res.fold({ "Backed up ✓" }, { it.message ?: "failed" })) } }
                        }
                        SmallBtn("Restore") {
                            vm.restoreFromGithub { res -> scope.launch { snackbar.showSnackbar(res.fold({ "Restored ✓" }, { it.message ?: "failed" })) } }
                        }
                        SmallBtn("Unlink", danger = true) { vm.unlinkGitHub(); scope.launch { snackbar.showSnackbar("Unlinked") } }
                    }
                }
            }

            MoreContent()
            Spacer(Modifier.height(8.dp))
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp))
    }
}

@Composable
private fun StatBlock(value: String, unit: String, label: String, note: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text(" $unit", fontSize = 12.sp, color = Ink)
        }
        Text(label, fontSize = 12.sp, color = Teal, fontWeight = FontWeight.SemiBold)
        if (note != null) Text(note, fontSize = 8.sp, color = Gray)
    }
}

@Composable
private fun MenuRow(title: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MenuInk, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Gray, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SmallBtn(label: String, teal: Boolean = false, danger: Boolean = false, enabled: Boolean = true, onClick: () -> Unit) {
    val bg = when { danger -> Color(0xFFFDECEC); teal -> Teal; else -> Color(0xFFE8F7F4) }
    val fg = when { danger -> Color(0xFFC2504A); teal -> Color.White; else -> Teal }
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
        color = if (enabled) fg else Gray, textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)).background(if (enabled) bg else Color(0xFFF0F0F0))
            .border(1.dp, Color(0x14000000), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .let { if (enabled) it.clickableNoInd(onClick) else it })
}
