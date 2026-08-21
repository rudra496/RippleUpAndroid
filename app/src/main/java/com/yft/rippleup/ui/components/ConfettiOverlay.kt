package com.yft.rippleup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.EmeraldLight
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.Purple
import com.yft.rippleup.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    var rotSpeed: Float,
    var size: Float,
    var color: Color,
    var leaf: Boolean,
    var alpha: Float,
)

/**
 * One-shot eco confetti burst — a leaf + circle particle explosion that fades.
 * Mirrors the web triggerConfetti(): ~45 particles launched from centre,
 * gravity, friction, and a mid-screen fade. Pass a changing [burstKey] to fire.
 */
@Composable
fun ConfettiOverlay(
    burstKey: Int,
    modifier: Modifier = Modifier,
) {
    if (burstKey == 0) return // not yet fired

    val particles = remember(burstKey) { spawnConfetti() }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(burstKey) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 1000, easing = LinearEasing))
    }

    Canvas(modifier.fillMaxSize()) {
        val t = progress.value
        for (p in particles) {
            stepParticle(p, t)
            if (p.alpha <= 0.02f) continue
            drawParticle(p)
        }
    }
}

private fun spawnConfetti(): List<Particle> {
    val colors = listOf(Emerald, EmeraldLight, SkyBlue, Gold, Purple)
    return List(26) {
        val angle = (Math.PI.toFloat() * 1.05f) + (Random.nextFloat() - 0.5f) * Math.PI.toFloat() * 0.5f
        val speed = 3.5f + Random.nextFloat() * 7f
        Particle(
            x = 0.5f, // normalised; scaled in draw step
            y = 0.7f,
            vx = cos(angle) * speed * 0.01f,
            vy = sin(angle) * speed * 0.01f,
            rotation = Random.nextFloat() * 360f,
            rotSpeed = (Random.nextFloat() - 0.5f) * 15f,
            size = 14f + Random.nextFloat() * 18f,
            color = colors.random(),
            leaf = Random.nextFloat() > 0.45f,
            alpha = 1f,
        )
    }
}

private fun DrawScope.stepParticle(p: Particle, t: Float) {
    // We advance the particle proportionally to global progress so the animation
    // is deterministic and finishes cleanly when t reaches 1.
    val frames = t * 60f
    p.x += p.vx * (frames * 0.05f)
    p.y += p.vy * (frames * 0.05f) + 0.006f * frames * frames // gravity accumulates
    p.vy += 0.0024f * frames
    p.vx *= 0.99f
    p.rotation += p.rotSpeed * 0.05f * frames
    if (p.y > 0.5f) p.alpha = (1f - (p.y - 0.5f) * 1.6f).coerceAtLeast(0f)
}

private fun DrawScope.drawParticle(p: Particle) {
    val cx = p.x * size.width
    val cy = p.y * size.height
    val sz = p.size
    if (p.leaf) {
        val path = Path().apply {
            moveTo(cx, cy - sz)
            quadraticTo(cx + sz * 1.4f, cy, cx, cy + sz)
            quadraticTo(cx - sz * 1.4f, cy, cx, cy - sz)
        }
        rotate(degrees = p.rotation, pivot = Offset(cx, cy)) {
            drawPath(path, color = p.color.copy(alpha = p.alpha))
        }
    } else {
        drawCircle(
            color = p.color.copy(alpha = p.alpha),
            radius = sz / 2,
            center = Offset(cx, cy),
        )
    }
}
