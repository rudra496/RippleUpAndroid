package com.yft.rippleup.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
 * A lightweight 3D-ish particle "eco-globe" rendered on Compose Canvas. This is
 * a deliberate, phone-friendly echo of the Three.js hero animation in the web
 * project: orbiting green/blue/gold nodes around a sphere, gently rotating, and
 * pulsing outward when [pulseKey] changes (an action was verified).
 *
 * Full Three.js fidelity would cost battery and APK size on mobile; this gives
 * the same visual language (rotating eco nodes, colour-coded pulses) at a
 * fraction of the cost.
 */
@Composable
fun EcoGlobe(
    modifier: Modifier = Modifier,
    pulseKey: Int = 0,
) {
    val infinite = rememberInfiniteTransition(label = "globe")
    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(24_000, easing = LinearEasing)),
        label = "spin",
    )
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulse",
    )

    val pulseScale = if (pulseKey > 0) 1f + 0.06f * (1f - pulse) else 1f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .scale(pulseScale),
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension * 0.36f

        // Three orbit rings (green, blue, gold) like the web rings.
        drawOrbit(cx, cy, r * 0.7f, rotation, EmeraldLight, 14)
        drawOrbit(cx, cy, r, rotation * 0.7f + 40f, SkyBlue, 18)
        drawOrbit(cx, cy, r * 1.25f, rotation * 0.5f + 90f, Gold, 12)

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

        // Outward shockwave when a pulse fires
        if (pulseKey > 0) {
            drawCircle(
                color = EmeraldLight.copy(alpha = (1f - pulse) * 0.6f),
                radius = r * (0.5f + pulse * 0.8f),
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
