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
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.components.LeafLogo
import com.yft.rippleup.ui.theme.BgDeep
import com.yft.rippleup.ui.theme.FieldBg
import com.yft.rippleup.ui.theme.Teal
import com.yft.rippleup.ui.theme.TealSoft
import kotlinx.coroutines.launch

/**
 * Figma-style onboarding: splash leaf mark -> how-it-works -> name entry.
 * Deep green background, teal accents, dark fields.
 */
@Composable
fun OnboardingScreen(vm: com.yft.rippleup.ui.StatsViewModel, onDone: (String) -> Unit) {
    val pager = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }

    var showAuth by remember { mutableStateOf(false) }
    if (showAuth) { AuthScreen(onDone = onDone); return }
    Box(Modifier.fillMaxSize().background(Color(0xFFF5FFFC))) {
        Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val topArt = if (pager.currentPage == 0) com.yft.rippleup.R.drawable.fig_hero
                else com.yft.rippleup.R.drawable.fig_intro
            Box(Modifier.fillMaxWidth().height(300.dp)) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(topArt),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
                when (page) {
                    0 -> SplashPage()
                    1 -> HowItWorks()
                    2 -> NamePage(name = name, onNameChange = { name = it })
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
                        showAuth = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Color(0xFF04241E)),
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
private fun SplashPage() {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Sustainability, Made Fun",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            "",
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "RipplUp rewards your sustainable habits — refills, recycling, green commutes — with points, streaks and community impact.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
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
            "Redeem points for discounts at local sustainable vendors, badges, and certificates."),
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Log Actions, Earn Points", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())
        steps.forEach { s ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(46.dp).clip(CircleShape).background(Teal.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(s.icon, contentDescription = null, tint = Teal) }
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

@Composable
private fun NamePage(name: String, onNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("What should we call you?", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text("We'll personalise your dashboard. This stays on your device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Your name") },
            placeholder = { Text("e.g. Ayaan") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg,
                focusedBorderColor = Teal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            ),
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
                        if (active) Teal
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
            )
        }
    }
}


@Composable
private fun GitHubVerifyPage(vm: com.yft.rippleup.ui.StatsViewModel) {
    val linked by vm.gitHubLinked.collectAsState()
    var token by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Outlined.Cloud,
            contentDescription = null,
            tint = Teal,
            modifier = Modifier.size(52.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text("Verify with GitHub", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "RipplUp requires a verified GitHub account before you can log actions. " +
                "Your claims are recorded to a private ledger in your own GitHub.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))

        if (linked) {
            Text("✓ Verified as @" + vm.gitHubSync.githubLogin(), style = MaterialTheme.typography.titleMedium,
                color = Teal, textAlign = TextAlign.Center)
        } else {
            Text(
                "Create a free token at github.com/settings/tokens (classic, 'gist' scope only) and paste it below:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("ghp_… or github_pat_…", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal,
                    unfocusedContainerColor = FieldBg,
                    focusedContainerColor = FieldBg,
                ),
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    vm.linkGitHub(token) { res ->
                        status = res.fold({ "✓ Verified as @$it" }, { it.message ?: "failed" })
                    }
                },
                enabled = token.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Color(0xFF04241E)),
            ) { Text("Verify & Link") }
            status?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            }
        }
    }
}
