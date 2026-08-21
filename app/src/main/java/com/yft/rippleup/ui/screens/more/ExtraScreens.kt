package com.yft.rippleup.ui.screens.more

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.util.clickableNoInd

// Exact Figma palette
private val Bg = Color(0xFFF5F6F6)
private val Card = Color(0xFFFFFFFF)
private val Teal = Color(0xFF0D9488)
private val Ink = Color(0xFF0C2620)
private val Muted = Color(0xFF5A8A82)
private val Mint = Color(0xFFE8F7F4)
private val Orange = Color(0xFFF07021)
private val Dark = Color(0xFF101828)
private val ChipInk = Color(0xFF973C00)

/** DISCOVER — exact: search bar, filter chips, partner cards with perks. */
@Composable
fun DiscoverScreen() {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("partners") }
    val partners = listOf(
        Triple("♻️", "EcoRecycle Hub", "+500 pts · Recycle plastic waste") to
            Pair("Unlock 2 x Ripple Points", "Visiting today!"),
        Triple("👕", "ThriftUp Store", "+500 pts · Donate unused clothing") to
            Pair("10% off next purchase", "Visit today!"),
        Triple("🥗", "Green Market", "+200 pts · Buy fresh local produce") to
            Pair("Fresh local produce", "Open today!"),
        Triple("💧", "AquaRefill Station", "+100 pts · Refill your bottle") to
            Pair("Stay hydrated", "Open now!"),
    )
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Discover", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Card)
            .border(1.dp, Color(0x14000000), RoundedCornerShape(14.dp)).padding(14.dp)) {
            if (query.isEmpty()) Text("Search actions, partners, events…", fontSize = 14.sp, color = Muted)
            androidx.compose.foundation.text.BasicTextField(
                value = query, onValueChange = { query = it }, singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Ink),
                modifier = Modifier.fillMaxWidth())
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("self-report", "partner-verified", "partners").forEach { c ->
                val active = filter == c
                Text(c, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color = if (active) Color.White else Muted,
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(if (active) Dark else Mint)
                        .clickableNoInd { filter = c }.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
        partners.filter { query.isBlank() || it.first.second.contains(query, true) }.forEach { (p, perk) ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
                .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(p.first, fontSize = 24.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(p.second, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(p.third, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Teal)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🎁 ${perk.first}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ChipInk)
                        Text(perk.second, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White,
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(Orange).padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                Text("Soon", fontSize = 10.sp, color = Muted, textAlign = TextAlign.End)
            }
        }
        Text("Partner verification will be available with a connected backend — feature coming soon.",
            fontSize = 10.sp, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

/** NOTIFICATIONS — exact: streak reminders + achievement + event invites. */
@Composable
fun NotificationsScreen() {
    val notes = listOf(
        Triple("🔥", "Don't break the streak!", "You logged 5 days in a row. One more ripple today keeps it alive.") to "2m ago",
        Triple("🏆", "Badge unlocked: Plastic Saver", "You avoided 10 plastic bottles. +150 Ripple Points added.") to "1h ago",
        Triple("📅", "Community Cleanup this Saturday", "Riverside Park · 9:00 AM. Join and earn +200 pts.") to "3h ago",
        Triple("✅", "Action verified", "Your QR report at EcoRecycle Hub was approved. +340 pts.") to "Yesterday",
        Triple("🌱", "Weekly impact summary", "You avoided 20 g CO₂e this week — 3% better than last week!") to "2d ago",
    )
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Notifications", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        notes.forEach { (n, time) ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
                .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)).padding(14.dp)) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(Mint), contentAlignment = Alignment.Center) {
                    Text(n.first, fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(n.second, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(n.third, fontSize = 12.sp, color = Muted)
                }
                Text(time, fontSize = 10.sp, color = Muted)
            }
        }
        Text("Push notifications will arrive with a connected backend — coming soon.",
            fontSize = 10.sp, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

/** EVENTS — exact: upcoming community events with register. */
@Composable
fun EventsScreen() {
    val events = listOf(
        Triple("🌳", "Community Cleanup", "Riverside Park · Sat, Sept 21 · 9:00 AM") to "+200 pts",
        Triple("🔧", "Eco Workshop: Repair Café", "Community Hall · Sun, Sept 29 · 2:00 PM") to "+150 pts",
        Triple("🚲", "Bike-to-Campus Day", "Meet at Main Gate · Fri, Oct 4 · 8:00 AM") to "+100 pts",
        Triple("👕", "Clothes Swap Market", "Central Square · Sat, Oct 12 · 10:00 AM") to "+120 pts",
    )
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Upcoming Events", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Join events, verify attendance with a QR, earn big points.",
            fontSize = 12.sp, color = Muted)
        events.forEach { (e, pts) ->
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Card)
                .border(1.dp, Color(0x14000000), RoundedCornerShape(16.dp)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Mint), contentAlignment = Alignment.Center) {
                    Text(e.first, fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(e.second, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(e.third, fontSize = 12.sp, color = Muted)
                    Text(pts, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Teal)
                }
                Text("Register", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Teal).padding(horizontal = 12.dp, vertical = 7.dp))
            }
        }
        Text("Event registration will be available with a connected backend — coming soon.",
            fontSize = 10.sp, color = Muted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

/** PERSONALISE — exact: interest chips (Hydration, Zero Waste, Recycling, Transport, Food). */
@Composable
fun PersonaliseScreen(onDone: (List<String>) -> Unit) {
    val all = listOf("💧 Hydration", "🌱 Zero Waste", "♻️ Recycling", "🚲 Transport", "🥗 Food")
    val picked = remember { mutableStateOf(setOf("🌱 Zero Waste", "🚲 Transport")) }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5FFFC)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(30.dp))
        Text("Personalisation", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text("Add your interests so we can personalise your RippleUp experience.",
            fontSize = 14.sp, color = Muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        all.forEach { chip ->
            val active = chip in picked.value
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(if (active) Mint else Card)
                .border(1.dp, Color(0x14000000), RoundedCornerShape(14.dp))
                .clickableNoInd {
                    picked.value = if (active) picked.value - chip else picked.value + chip
                }
                .padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(chip.take(2), fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Text(chip.drop(2), fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = if (active) Teal else Ink)
            }
            Spacer(Modifier.height(10.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text("Done", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Teal)
                .clickableNoInd { onDone(picked.value.toList()) }.padding(vertical = 14.dp),
            textAlign = TextAlign.Center)
    }
}
