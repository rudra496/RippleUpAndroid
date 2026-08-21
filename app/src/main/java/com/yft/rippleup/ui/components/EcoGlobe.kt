package com.yft.rippleup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.EmeraldLight
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.SkyBlue
import kotlin.math.cos
import kotlin.math.sin

/**
 * A lightweight particle "eco-globe" rendered on Compose Canvas — an echo of
 * the Three.js hero from the web project. Performance note: orbits are drawn
 * statically (no infinite animation loops) so the onboarding screen costs
 * nothing on the CPU; a one-shot pulse plays only when [pulseKey] changes.
 */
@Composable
fun EcoGlobe(
    modifier: Modifier = Modifier,
    pulseKey: Int = 0,
) {
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(pulseKey) {
        if (pulseKey > 0) {
            pulse.snapTo(0f)
            pulse.animateTo(1f, tween(1200, easing = LinearEasing))
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .scale(1f + 0.05f * (1f - pulse.value)),
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.36f

        // Three orbit rings (green, blue, gold) — static positions.
        drawOrbit(cx, cy, r * 0.7f, 20f, EmeraldLight, 14)
        drawOrbit(cx, cy, r, 55f, SkyBlue, 18)
        drawOrbit(cx, cy, r * 1.25f, 95f, Gold, 12)

        // Core glow
        drawCircle(
            color = Emerald.copy(alpha = 0.25f),
            radius = r * 0.45f,
            center = Offset(cx, cy),
        )
        drawCircle(
            color = Emerald.copy(alpha = 0.6f),
            radius = r * 0.2f,
            center = Offset(cx, cy),
        )

        // One-shot outward shockwave on verified actions
        if (pulseKey > 0 && pulse.value < 1f) {
            drawCircle(
                color = EmeraldLight.copy(alpha = (1f - pulse.value) * 0.6f),
                radius = r * (0.5f + pulse.value * 0.8f),
                center = Offset(cx, cy),
                style = Stroke(width = 3f),
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOrbit(
    cx: Float,
    cy: Float,
    radius: Float,
    rotationDeg: Float,
    color: Color,
    nodeCount: Int,
) {
    // Faint orbit path
    drawCircle(
        color = color.copy(alpha = 0.15f),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5f),
    )
    // Nodes
    val rad = Math.toRadians(rotationDeg.toDouble()).toFloat()
    val yTilt = 0.38f // squashed to fake 3D tilt
    for (i in 0 until nodeCount) {
        val angle = rad + (i / nodeCount.toFloat()) * (2f * Math.PI.toFloat())
        val nx = cx + radius * cos(angle)
        val ny = cy + radius * sin(angle) * yTilt
        // depth-based alpha: nodes on the "back" are dimmer
        val depth = (sin(angle) + 1f) / 2f
        drawCircle(
            color = color.copy(alpha = 0.4f + 0.6f * depth),
            radius = 6f,
            center = Offset(nx, ny),
        )
    }
}
