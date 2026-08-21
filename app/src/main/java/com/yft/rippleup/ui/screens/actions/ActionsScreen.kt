package com.yft.rippleup.ui.screens.actions

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.data.model.EcoAction
import com.yft.rippleup.data.repo.LogResult
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.util.clickableNoInd
import kotlinx.coroutines.launch

// Exact Figma colors
private val Bg = Color(0xFFF5F6F6)
private val Card = Color(0xFFFFFFFF)
private val Mint = Color(0xFFE8F7F4)
private val Ink = Color(0xFF0C2620)
private val Muted = Color(0xFF5A8A82)
private val Teal = Color(0xFF0D9488)
private val TagBg = Color(0xFFE8E3DE)
private val TagInk = Color(0xFF666666)
private val Orange = Color(0xFFF07021)
private val OrangeInk = Color(0xFFFFFDF7)

/**
 * CHOOSE ACTION — exact rebuild: light bg, title header, "Today's Remaining
 * Ripples list" + "Something else?" sections; each action a timeline row with
 * a Self Report button (guarded logging, snackbar feedback).
 */
@Composable
fun ActionsScreen(vm: StatsViewModel) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val busy = remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(Bg)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).clip(CircleShape).background(Mint))
                Spacer(Modifier.width(10.dp))
                Text("Choose Action", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
            }

            Text("Today’s Remaining Ripples list", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)

            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Card)
                    .border(1.dp, Color(0x14000000), RoundedCornerShape(24.dp)).padding(14.dp),
            ) {
                EcoAction.entries.forEach { action ->
                    ActionRow(action) {
                        if (busy.value) return@ActionRow
                        busy.value = true
                        vm.logAction(
                            actionKey = actionKey(action), title = action.title,
                            points = action.points, co2Kg = action.co2Kg,
                            colorTag = colorTagFor(action), iconTag = action.iconName,
                        ) { result ->
                            busy.value = false
                            scope.launch {
                                when (result) {
                                    is LogResult.Success -> snackbar.showSnackbar("Verified! +${result.points} pts")
                                    is LogResult.Rejected -> snackbar.showSnackbar(result.reason)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }

            Text("Something else?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("Scan a partner QR code from the Scan tab to verify partner actions.",
                fontSize = 12.sp, color = Muted)

            Spacer(Modifier.height(8.dp))
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp))
    }
}

@Composable
private fun ActionRow(action: EcoAction, onReport: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(23.dp).clip(CircleShape).background(action.color.copy(alpha = 0.18f))
                .border(2.dp, Card, CircleShape))
            Spacer(Modifier.height(4.dp))
            Box(Modifier.width(1.dp).height(28.dp).background(Color(0x1F0D9488)))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(action.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF000000))
            Text("+${action.points} pts · −${action.co2Kg} kg CO₂", fontSize = 12.sp, color = Muted)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Tag("Self report", bg = TagBg, text = TagInk)
                Tag("Report", bg = Orange, text = OrangeInk, onClick = onReport)
            }
        }
    }
}

@Composable
private fun Tag(label: String, bg: Color, text: Color, onClick: (() -> Unit)? = null) {
    val mod = if (onClick != null) Modifier
        .clip(RoundedCornerShape(8.dp)).background(bg)
.then(com.yft.rippleup.util.clickableNoInd(onClick))
        .padding(horizontal = 10.dp, vertical = 3.dp)
    else Modifier.clip(RoundedCornerShape(8.dp)).background(bg).padding(horizontal = 8.dp, vertical = 2.dp)
    Box(mod) { Text(label, fontSize = 10.sp, color = text, fontWeight = if (onClick != null) FontWeight.Medium else FontWeight.Normal) }
}

private fun actionKey(action: EcoAction): String = when (action) {
    EcoAction.Refill -> "refill"; EcoAction.Food -> "food"
    EcoAction.Recycle -> "recycle"; EcoAction.Transit -> "transit"
}
private fun colorTagFor(action: EcoAction): String = when (action) {
    EcoAction.Refill -> "blue"; EcoAction.Food -> "red"
    EcoAction.Recycle -> "green"; EcoAction.Transit -> "purple"
}
