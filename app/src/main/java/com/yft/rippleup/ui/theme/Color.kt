package com.yft.rippleup.ui.theme

import androidx.compose.ui.graphics.Color


// --- EXTRACTED DIRECTLY FROM FIGMA API (RipplUp UN MVP) — exact values ---
val FiggBg = Color(0xFFF5F6F6)        // screen background (light)
val FigCard = Color(0xFFFFFFFF)       // white cards r=8
val FigTeal = Color(0xFF0D9488)       // primary (800 uses!)
val FigInk = Color(0xFF0C2620)        // main text (dark green-black)
val FigMuted = Color(0xFF5A8A82)      // secondary text
val FigSoftTeal = Color(0xFF79B6AC)   // soft teal
val FigMint = Color(0xFFE8F7F4)       // mint surface (163 uses)
val FigOrange = Color(0xFFFB923C)     // accent orange
val FigDeepOrange = Color(0xFFF07021) // deep orange CTA
val FigGold = Color(0xFFF9D14C)       // calendar check gold
val FigPink = Color(0xFFF472B6)       // pink accent
val FigBlue = Color(0xFF60A5FA)       // blue accent
val FigAmber = Color(0xFFFE9A00)      // amber
val FigGray = Color(0xFF8F8F8F)       // gray text

// ---------------------------------------------------------------------------
// RipplUp palette — ported from the "RipplUp UN MVP" Figma design.
// Dark forest-green app shell with teal primary and warm/cool accents.
// ---------------------------------------------------------------------------

// App shell backgrounds (dark forest green)
val BgDeep = FiggBg        // deepest screen background (splash green)
val BgSurface = FiggBg     // main screen background (near-black green)
val CardDark = FigCard      // raised card surface
val CardDarkHi = FigMint    // card hover/pressed state
val FieldBg = FigMint       // light mint field card (Figma forms)

// Primary — teal green (Figma #009080 family)
val Teal = FigTeal          // primary buttons / accents
val TealLight = Color(0xFF14B8A6)     // lighter variant
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
val TextPrimary = FigInk   // near-white with green cast
val TextSecondary = FigMuted
val TextMuted = Color(0xFF6F8A7D)

// Strokes / dividers on dark
val Stroke = Color(0x140C2620)        // 15% white hairline
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
val Emerald = FigTeal
val EmeraldLight = TealLight
val EmeraldDark = Color(0xFF03746A)
val GlassPanel = CardDark
val GlassPanelHi = CardDarkHi
val StrokeSubtle = StrokeSoft
