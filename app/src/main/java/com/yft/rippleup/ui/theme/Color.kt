package com.yft.rippleup.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// RipplUp palette — ported from the "RipplUp UN MVP" Figma design.
// Dark forest-green app shell with teal primary and warm/cool accents.
// ---------------------------------------------------------------------------

// App shell backgrounds (dark forest green)
val BgDeep = Color(0xFF07211A)        // deepest screen background (splash green)
val BgSurface = Color(0xFF0D1512)     // main screen background (near-black green)
val CardDark = Color(0xFF12211B)      // raised card surface
val CardDarkHi = Color(0xFF183026)    // card hover/pressed state
val FieldBg = Color(0xFFE8F7F4)       // light mint field card (Figma forms)

// Primary — teal green (Figma #009080 family)
val Teal = Color(0xFF00907F)          // primary buttons / accents
val TealLight = Color(0xFF10A08F)     // lighter variant
val TealSoft = Color(0xFFB0F2EB)      // pale mint (text on dark, highlights)
val MintTint = Color(0xFFE8F7F4)      // lightest mint tint

// Accents
val Orange = Color(0xFFF07020)        // streak / fire / energy CTA
val Gold = Color(0xFFF0D070)          // badges / gold rank
val GoldDeep = Color(0xFFD9A93F)
val Cyan = Color(0xFF30C0D0)          // stats / info accents
val LimeGreen = Color(0xFF80E080)     // success / CO2 saved
val Coral = Color(0xFFEF6A4A)         // food / warning
val SkyBlue = Color(0xFF4AA8E0)       // water / refill
val Purple = Color(0xFF9B7EF0)        // transit / special

// Text
val TextPrimary = Color(0xFFF2FBF6)   // near-white with green cast
val TextSecondary = Color(0xFFA8C7B6)
val TextMuted = Color(0xFF6F8A7D)

// Strokes / dividers on dark
val Stroke = Color(0x26FFFFFF)        // 15% white hairline
val StrokeSoft = Color(0x14FFFFFF)

// Podium / rank colors (leaderboard)
val RankGold = Color(0xFFF5C044)
val RankSilver = Color(0xFFB8C0CC)
val RankBronze = Color(0xFFCD8F5A)

// SDG chip colors (from the web sdg-card values, retained for the info hub)
val Sdg2 = Color(0xFFE5243B)
val Sdg3 = Color(0xFF4C9F38)
val Sdg11 = Color(0xFFF99D1C)
val Sdg12 = Color(0xFFCF8D2E)
val Sdg13 = Color(0xFF3F7E44)
val Sdg15 = Color(0xFF56C02B)

// ---------------------------------------------------------------------------
// Backward-compatible aliases — screens still being restyled reference these.
// They map the old (web-port) names onto the new Figma palette.
// ---------------------------------------------------------------------------
val Emerald = Teal
val EmeraldLight = TealLight
val EmeraldDark = Color(0xFF03746A)
val GlassPanel = CardDark
val GlassPanelHi = CardDarkHi
val StrokeSubtle = StrokeSoft
