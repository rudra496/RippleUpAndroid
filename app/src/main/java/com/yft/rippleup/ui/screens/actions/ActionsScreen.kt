package com.yft.rippleup.ui.screens.actions

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yft.rippleup.data.model.EcoAction
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.SectionHeader
import com.yft.rippleup.ui.components.ecoIcon
import com.yft.rippleup.ui.theme.Stroke

/** Pick a sustainable action to log. Mirrors the simulator action buttons. */
@Composable
fun ActionsScreen(vm: StatsViewModel) {
    val pulse by vm.pulseTick.collectAsState()
    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                title = "Log an Eco Action",
                subtitle = "Tap an action to verify it. You'll earn points and watch your impact grow.",
            )

            EcoAction.entries.forEach { action ->
                ActionCard(action = action) {
                    vm.logAction(
                        title = action.title,
                        points = action.points,
                        co2Kg = action.co2Kg,
                        colorTag = colorTagFor(action),
                        iconTag = action.iconName,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActionCard(action: EcoAction, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
        onClick = onClick,
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

private fun colorTagFor(action: EcoAction): String = when (action) {
    EcoAction.Refill -> "blue"
    EcoAction.Food -> "red"
    EcoAction.Recycle -> "green"
    EcoAction.Transit -> "purple"
}
