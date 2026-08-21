package com.yft.rippleup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * RipplUp is a deliberately dark, forest-green app (per the Figma "RipplUp UN
 * MVP" design). We always render the dark palette — the brand identity is part
 * of the product, so the system setting is ignored.
 */
private val RippleColors = darkColorScheme(
    primary = Teal,
    onPrimary = TextPrimary,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = TealLight,
    onSecondary = BgDeep,
    tertiary = Gold,
    onTertiary = BgDeep,
    background = BgSurface,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = Stroke,
    outlineVariant = StrokeSoft,
    error = Coral,
    onError = TextPrimary,
)

@Composable
fun RippleUpTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = RippleColors,
        typography = RippleTypography,
        content = content,
    )
}
