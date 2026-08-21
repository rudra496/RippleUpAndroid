package com.yft.rippleup.ui.screens.actions

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yft.rippleup.data.model.EcoAction
import com.yft.rippleup.data.repo.LogResult
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.SectionHeader
import com.yft.rippleup.ui.components.ecoIcon
import com.yft.rippleup.ui.theme.CardDark
import com.yft.rippleup.ui.theme.Stroke
import com.yft.rippleup.ui.theme.Teal
import kotlinx.coroutines.launch

/**
 * Log an eco action — production-guarded. Each card shows its live cooldown
 * state; taps are debounced and rejections (cooldown/daily cap/tamper) are
 * surfaced via snackbar instead of silently awarding points.
 */
@Composable
fun ActionsScreen(vm: StatsViewModel) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // per-action lock to debounce rapid taps
    val busy = remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                title = "Log an Eco Action",
                subtitle = "Verified actions earn points. Cooldowns and daily limits keep it fair.",
            )

            EcoAction.entries.forEach { action ->
                ActionCard(
                    action = action,
                    enabled = !busy.value,
                    onClick = {
                        if (busy.value) return@ActionCard
                        busy.value = true
                        vm.logAction(
                            actionKey = actionKey(action),
                            title = action.title,
                            points = action.points,
                            co2Kg = action.co2Kg,
                            colorTag = colorTagFor(action),
                            iconTag = action.iconName,
                        ) { result ->
                            busy.value = false
                            scope.launch {
                                when (result) {
                                    is LogResult.Success ->
                                        snackbar.showSnackbar("Verified! +${result.points} pts · −${result.co2Kg} kg CO₂")
                                    is LogResult.Rejected ->
                                        snackbar.showSnackbar(result.reason)
                                }
                            }
                        }
                    },
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
        )
    }
}

@Composable
private fun ActionCard(action: EcoAction, enabled: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().alpha(if (enabled) 1f else 0.55f),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
        onClick = { if (enabled) onClick() },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(52.dp).clip(CircleShape).background(action.color.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(ecoIcon(action.iconName), contentDescription = action.title,
                    tint = action.color, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(action.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("+${action.points} Points  •  −${action.co2Kg} kg CO₂",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun actionKey(action: EcoAction): String = when (action) {
    EcoAction.Refill -> "refill"
    EcoAction.Food -> "food"
    EcoAction.Recycle -> "recycle"
    EcoAction.Transit -> "transit"
}

private fun colorTagFor(action: EcoAction): String = when (action) {
    EcoAction.Refill -> "blue"
    EcoAction.Food -> "red"
    EcoAction.Recycle -> "green"
    EcoAction.Transit -> "purple"
}
