package com.yft.rippleup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * RippleUp is a deliberately dark, glassy theme. We do not offer a light scheme —
 * the brand identity (dark green glassmorphism) is part of the product, so we
 * ignore the system setting and always render the dark palette.
 */
private val RippleColors = darkColorScheme(
    primary = Emerald,
    onPrimary = BgDeep,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = TextPrimary,
    secondary = EmeraldLight,
    onSecondary = BgDeep,
    tertiary = Gold,
    onTertiary = BgDeep,
    background = BgDeep,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassPanel,
    onSurfaceVariant = TextSecondary,
    outline = Stroke,
    outlineVariant = StrokeSubtle,
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
