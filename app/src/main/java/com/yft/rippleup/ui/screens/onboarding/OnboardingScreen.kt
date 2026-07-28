package com.yft.rippleup.ui.screens.onboarding

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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.components.EcoGlobe
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.components.GradientText
import com.yft.rippleup.ui.theme.BgDeep
import com.yft.rippleup.ui.theme.Emerald
import kotlinx.coroutines.launch

/**
 * Three-page intro (mission, how it works, name) then "Get Started" lands the
 * user on the dashboard. Mirrors the hero + about cards from the web.
 */
@Composable
fun OnboardingScreen(onDone: (String) -> Unit) {
    val pager = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize().background(BgDeep)) {
        Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Eco-globe up top, echoing the web hero animation.
            Box(Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                EcoGlobe(modifier = Modifier.size(220.dp))
            }

            HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
                when (page) {
                    0 -> IntroPage(
                        headline = "Turn Everyday",
                        highlight = "Sustainable Actions",
                        tail = "Into Rewards",
                        body = "RippleUp turns eco habits — refilling bottles, recycling, green commuting — into points, streaks, and community impact.",
                    )
                    1 -> HowItWorks()
                    2 -> NamePage(
                        name = name,
                        onNameChange = { name = it },
                    )
                }
            }

            Dots(currentPage = pager.currentPage, pageCount = 3)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val p = pager.currentPage
                    if (p < 2) {
                        scope.launch { pager.animateScrollToPage(p + 1) }
                    } else {
                        onDone(name)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Emerald, contentColor = BgDeep),
            ) {
                Text(
                    if (pager.currentPage < 2) "Continue" else "Start Earning Points",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.size(8.dp))
                Icon(Icons.Outlined.ArrowForward, contentDescription = null)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun IntroPage(headline: String, highlight: String, tail: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 24) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(headline, style = MaterialTheme.typography.displayMedium, textAlign = TextAlign.Center)
                GradientText(highlight, style = MaterialTheme.typography.displayMedium)
                Text(tail, style = MaterialTheme.typography.displayMedium, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text(body, style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

private data class Step(val icon: ImageVector, val title: String, val body: String)

@Composable
private fun HowItWorks() {
    val steps = listOf(
        Step(Icons.Outlined.QrCodeScanner, "1. Scan & Verify",
            "Scan QR codes at water refill stations, recycling hubs, and green vendors to log actions."),
        Step(Icons.Outlined.Recycling, "2. Track Impact",
            "Watch your CO₂ savings, plastic avoided, and streak build in real time."),
        Step(Icons.Outlined.Redeem, "3. Unlock Rewards",
            "Earn points, badges, and discounts at local sustainable vendors."),
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("How RippleUp Works", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        steps.forEach { s ->
            GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20,
                contentPadding = PaddingValues(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(46.dp).clip(CircleShape).background(Emerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) { Icon(s.icon, contentDescription = null, tint = Emerald) }
                    Spacer(Modifier.size(12.dp))
                    Column {
                        Text(s.title, style = MaterialTheme.typography.titleMedium)
                        Text(s.body, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun NamePage(name: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("What should we call you?", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text("We'll personalise your dashboard. You can keep earning anonymously — this stays on your device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Your name") },
            placeholder = { Text("e.g. Rudra") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
        )
    }
}

@Composable
private fun Dots(currentPage: Int, pageCount: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { i ->
            val active = i == currentPage
            Box(
                Modifier
                    .height(8.dp)
                    .then(if (active) Modifier.width(24.dp) else Modifier.width(8.dp))
                    .clip(CircleShape)
                    .background(
                        if (active) Emerald
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
            )
        }
    }
}
