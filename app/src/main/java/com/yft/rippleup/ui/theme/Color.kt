package com.yft.rippleup.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// RippleUp palette — ported from the web project's HSL color system.
// The web site uses a dark glassmorphism theme with emerald + gold + purple
// accents on a near-black green-tinted background.
// ---------------------------------------------------------------------------

// Backgrounds (dark, green-tinted near-black)
val BgDeep = Color(0xFF0A1410)        // page background
val BgSurface = Color(0xFF102018)     // elevated surface
val GlassPanel = Color(0xFF163024)    // glass card fill
val GlassPanelHi = Color(0xFF1E3D2E)  // hover/pressed glass

// Brand greens (emerald primary)
val Emerald = Color(0xFF10B981)       // primary  (web --primary)
val EmeraldLight = Color(0xFF34D399)  // mint     (web --accent)
val EmeraldDark = Color(0xFF059669)

// Accents
val Gold = Color(0xFFF59E0B)          // warning / bronze streaks (web --warning)
val GoldDark = Color(0xFFD97706)
val Purple = Color(0xFF8B5CF6)        // eco legend tier (web --purple)
val SkyBlue = Color(0xFF3B82F6)       // refill/water (web --info)
val Coral = Color(0xFFEF4444)         // food / danger (web --danger)
val Orange = Color(0xFFF97316)        // streak fire

// SDG chip colors (from the web sdg-card --sdg-color values)
val Sdg2 = Color(0xFFE5243B)
val Sdg3 = Color(0xFF4C9F38)
val Sdg11 = Color(0xFFF99D1C)
val Sdg12 = Color(0xFFCF8D2E)
val Sdg13 = Color(0xFF3F7E44)
val Sdg15 = Color(0xFF56C02B)

// Text
val TextPrimary = Color(0xFFF2FBF6)
val TextSecondary = Color(0xFFA8C7B6)
val TextMuted = Color(0xFF6F8A7D)

// Strokes / dividers
val Stroke = Color(0x33FFFFFF)        // 20% white for glass borders
val StrokeSubtle = Color(0x14FFFFFF)
