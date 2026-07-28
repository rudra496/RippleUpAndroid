package com.yft.rippleup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.theme.GlassPanelHi
import com.yft.rippleup.ui.theme.Stroke

/**
 * The signature glassmorphism surface used across the web project: a translucent
 * green card with a soft inner gradient and a thin 20%-white border. Compose
 * cannot do true backdrop blur below API 31, so we approximate the look with a
 * layered gradient + subtle elevation — visually faithful on every device.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 20,
    glow: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit,
) {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val gradient = Brush.linearGradient(
        colors = listOf(base, GlassPanelHi.copy(alpha = 0.55f)),
    )
    val shape = RoundedCornerShape(cornerRadius.dp)

    val baseModifier = modifier
        .then(if (glow) Modifier.shadow(20.dp, shape) else Modifier)
        .clip(shape)
        .border(BorderStroke(1.dp, Stroke), shape)
        .drawBehind {
            drawRoundRect(
                brush = gradient,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toFloat()),
            )
        }

    Box(baseModifier.padding(contentPadding)) { content() }
}
