package com.yft.rippleup.ui.screens.more

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yft.rippleup.R
import com.yft.rippleup.ui.components.GlassPanel
import com.yft.rippleup.ui.components.GradientText
import com.yft.rippleup.ui.components.SectionHeader
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.Stroke

/** The info hub: about, SDGs, journey, research, legal — all from the web. */
@Composable
fun MoreScreen() {
    val scroll = rememberScrollState()
    var openLegal by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Hero / brand
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(Emerald.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Public, contentDescription = null, tint = Emerald, modifier = Modifier.size(32.dp)) }
                Spacer(Modifier.height(8.dp))
                GradientText("Ripple", style = MaterialTheme.typography.displayMedium)
                Text("Empowering urban youth to make climate-positive choices, one small action at a time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }

            AboutSection()
            SdgSection()
            JourneySection()
            ResearchSection()
            LegalSection(onOpen = { openLegal = it })
            TeamFooter()

            Spacer(Modifier.height(8.dp))
        }

        AnimatedVisibility(visible = openLegal != null, modifier = Modifier.fillMaxSize()) {
            openLegal?.let { LegalReader(docKey = it, onClose = { openLegal = null }) }
        }
    }
}

@Composable
private fun AboutSection() {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column {
            Text("The Power of Small Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Every decision you make ripples through your community. Here is how RippleUp turns small efforts into habits.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            AboutCard("1. Scan & Verify", "Scan QR codes at water refill stations, recycling hubs, and green vendors to log actions instantly.")
            AboutCard("2. Track Impact", "Visualize your contributions: plastic bottles avoided, food waste prevented, and CO₂ reductions.")
            AboutCard("3. Unlock Rewards", "Redeem points for discounts at local sustainable vendors, badges, and recognition certificates.")
        }
    }
}

@Composable
private fun AboutCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Emerald)
            Text(body, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SdgSection() {
    Column {
        Text("Target Sustainable Development Goals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        ContentData.sdgs.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { sdg ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = sdg.color.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, sdg.color.copy(alpha = 0.4f)),
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(32.dp).clip(CircleShape).background(sdg.color),
                                contentAlignment = Alignment.Center,
                            ) { Text("${sdg.number}", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(8.dp))
                            Text(sdg.name, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun JourneySection() {
    var index by remember { mutableIntStateOf(0) }
    val step = ContentData.journey[index]
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column {
            Text("The RippleUp Journey", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Step ${index + 1} of 10", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("${step.num}", style = MaterialTheme.typography.displayMedium, color = Emerald)
                    Text(step.heading, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(step.desc, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                NavBtn("‹ Prev", index > 0) { index = (index - 1).coerceAtLeast(0) }
                NavBtn("Next ›", index < 9) { index = (index + 1).coerceAtMost(9) }
            }
        }
    }
}

@Composable
private fun NavBtn(label: String, enabled: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Emerald else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
        onClick = { if (enabled) onClick() },
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            fontWeight = FontWeight.Bold,
            color = if (enabled) androidx.compose.ui.graphics.Color(0xFF0A1410)
            else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ResearchSection() {
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 22) {
        Column {
            Text("Climate Insights & Research", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("The background, surveys, and stakeholder interviews behind RippleUp.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            // Survey bar charts
            ContentData.charts.forEach { c ->
                BarChartRow(label = c.label, pct = c.pct, color = when {
                    c.danger -> androidx.compose.ui.graphics.Color(0xFFEF4444)
                    c.info -> androidx.compose.ui.graphics.Color(0xFF3B82F6)
                    else -> Emerald
                })
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(12.dp))

            // Research pillars
            ContentData.research.forEach { r ->
                ResearchPillarCard(r)
                Spacer(Modifier.height(8.dp))
            }

            // Quotes
            ContentData.quotes.forEach { (who, q) ->
                QuoteCard(who, q)
                Spacer(Modifier.height(6.dp))
            }

            // Big stat box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Emerald.copy(alpha = 0.15f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Emerald.copy(alpha = 0.4f)),
            ) {
                Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(ContentData.statBox.first, style = MaterialTheme.typography.displayMedium, color = Emerald)
                    Text(ContentData.statBox.second, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun BarChartRow(label: String, pct: Float, color: androidx.compose.ui.graphics.Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text("${"%.1f".format(pct)}%", style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        ) {
            Box(
                Modifier.fillMaxWidth(pct / 100f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(color),
            )
        }
    }
}

@Composable
private fun ResearchPillarCard(r: ContentData.Research) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(r.title, style = MaterialTheme.typography.titleMedium, color = r.accent, fontWeight = FontWeight.Bold)
            Text(r.desc, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (r.extra != null) {
                Text(r.extra, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@Composable
private fun QuoteCard(who: String, quote: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(who, style = MaterialTheme.typography.labelLarge, color = Emerald, fontWeight = FontWeight.Bold)
            Text(quote, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LegalSection(onOpen: (String) -> Unit) {
    Column {
        Text("Legal & Support", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        ContentData.legalDocs.forEach { doc ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Stroke),
                onClick = { onOpen(doc.key) },
            ) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(doc.title, style = MaterialTheme.typography.titleMedium)
                        Text("Updated ${doc.updated}", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Outlined.ChevronRight, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun LegalReader(docKey: String, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val text = remember(docKey) {
        val resId = when (docKey) {
            "legal" -> R.raw.legal
            "privacy" -> R.raw.privacy
            "terms" -> R.raw.terms
            "cookies" -> R.raw.cookies
            else -> R.raw.legal
        }
        ctx.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.9f))
            .padding(16.dp),
    ) {
        GlassPanel(modifier = Modifier.fillMaxSize(), cornerRadius = 22) {
            Column(Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        ContentData.legalDocs.first { it.key == docKey }.title,
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Outlined.Close, contentDescription = "Close")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun TeamFooter() {
    val ctx = LocalContext.current
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Youth for Tomorrow", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("© 2026 RippleUp · Youth for Tomorrow. All rights reserved.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LinkChip("GitHub") { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rudra496"))) }
                LinkChip("Source") { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rudra496/ytf"))) }
            }
        }
    }
}

@Composable
private fun LinkChip(label: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(containerColor = Emerald.copy(alpha = 0.2f)),
        onClick = onClick,
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = Emerald, fontWeight = FontWeight.SemiBold)
    }
}
