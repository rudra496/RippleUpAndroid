package com.yft.rippleup.ui.screens.rewards

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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.StatsViewModel
import com.yft.rippleup.ui.components.SectionHeader
import com.yft.rippleup.ui.components.ecoIcon
import com.yft.rippleup.ui.theme.BgSurface
import com.yft.rippleup.ui.theme.CardDark
import com.yft.rippleup.ui.theme.Stroke
import com.yft.rippleup.ui.theme.Teal
import kotlinx.coroutines.launch

/**
 * Rewards screen per the Figma: balance banner on top, then vertical reward
 * cards with icon, title, partner, points cost and a working Redeem button
 * that deducts points from the user's balance.
 */
@Composable
fun RewardsScreen(vm: StatsViewModel) {
    val snapshot by vm.snapshot.collectAsState()
    val balance = snapshot?.points ?: 0
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SectionHeader(
                title = "Rewards",
                subtitle = "Redeem your RipplUp points at partner vendors.",
            )

            // Balance banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Teal.copy(alpha = 0.16f)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Your balance", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$balance pts", style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null,
                        tint = Teal, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(Modifier.height(4.dp))

            RewardsCatalog.rewards.forEach { reward ->
                RewardCard(
                    reward = reward,
                    affordable = balance >= reward.pointsCost,
                    onRedeem = {
                        vm.redeem(reward.pointsCost) { ok ->
                            scope.launch {
                                snackbar.showSnackbar(
                                    if (ok) "Redeemed: ${reward.title} 🎉"
                                    else "Not enough points yet — need ${reward.pointsCost - balance} more"
                                )
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
private fun RewardCard(reward: Reward, affordable: Boolean, onRedeem: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(52.dp).clip(CircleShape).background(reward.tint.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(ecoIcon(reward.iconTag), contentDescription = reward.title,
                    tint = reward.tint, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(reward.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(reward.partner, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text("${reward.pointsCost} pts", style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold, color = reward.tint)
            }
            RedeemButton(affordable = affordable, onRedeem = onRedeem)
        }
    }
}

@Composable
private fun RedeemButton(affordable: Boolean, onRedeem: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (affordable) Teal else CardDark,
        ),
        border = if (affordable) null else androidx.compose.foundation.BorderStroke(1.dp, Stroke),
        onClick = { if (affordable) onRedeem() },
    ) {
        Text(
            "Redeem",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontWeight = FontWeight.Bold,
            color = if (affordable) Color(0xFF04241E) else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
