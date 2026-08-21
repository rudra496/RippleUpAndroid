package com.yft.rippleup.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.theme.BgDeep
import com.yft.rippleup.ui.theme.Teal
import com.yft.rippleup.ui.theme.TealLight
import com.yft.rippleup.ui.theme.TealSoft

/**
 * The two-leaf logo mark from the Figma splash: a darker and a lighter leaf
 * leaning toward each other inside a soft halo, drawn with Canvas paths so no
 * image assets are needed.
 */
@Composable
fun LeafLogo(modifier: Modifier = Modifier, size: Dp = 140.dp) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            // Soft halo behind the leaves
            drawCircle(
                color = Teal.copy(alpha = 0.12f),
                radius = w * 0.46f,
                center = Offset(cx, cy),
            )
            drawCircle(
                color = Teal.copy(alpha = 0.06f),
                radius = w * 0.48f,
                center = Offset(cx, cy),
            )

            // Left leaf — darker teal, tilted left
            rotate(degrees = -18f, pivot = Offset(cx, cy)) {
                drawLeaf(
                    color = Teal,
                    veinColor = BgDeep,
                    center = Offset(cx - w * 0.10f, cy),
                    leafW = w * 0.30f,
                    leafH = h * 0.34f,
                )
            }
            // Right leaf — lighter teal, tilted right, slightly higher
            rotate(degrees = 14f, pivot = Offset(cx, cy)) {
                drawLeaf(
                    color = TealLight,
                    veinColor = BgDeep,
                    center = Offset(cx + w * 0.12f, cy - h * 0.03f),
                    leafW = w * 0.26f,
                    leafH = h * 0.30f,
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(
    color: Color,
    veinColor: Color,
    center: Offset,
    leafW: Float,
    leafH: Float,
) {
    val (cx, cy) = center
    // Classic leaf shape: two quadratic curves meeting at tip and base
    val path = Path().apply {
        moveTo(cx, cy - leafH)                       // tip (top)
        quadraticTo(cx + leafW, cy - leafH * 0.15f, cx, cy + leafH) // right belly -> base
        quadraticTo(cx - leafW, cy - leafH * 0.15f, cx, cy - leafH) // left belly -> tip
        close()
    }
    drawPath(path, color = color)

    // Central vein
    drawLine(
        color = veinColor.copy(alpha = 0.55f),
        start = Offset(cx, cy - leafH * 0.82f),
        end = Offset(cx, cy + leafH * 0.82f),
        strokeWidth = 3f,
    )
    // A few lateral veins
    for (t in listOf(-0.45f, -0.1f, 0.25f)) {
        val y = cy + leafH * t
        drawLine(
            color = veinColor.copy(alpha = 0.35f),
            start = Offset(cx, y),
            end = Offset(cx + leafW * 0.45f, y + leafH * 0.14f),
            strokeWidth = 2f,
        )
        drawLine(
            color = veinColor.copy(alpha = 0.35f),
            start = Offset(cx, y),
            end = Offset(cx - leafW * 0.45f, y + leafH * 0.14f),
            strokeWidth = 2f,
        )
    }
}

/**
 * Splash content: the leaf mark centered on the deep green, "RipplUp" wordmark
 * beneath — matching the Figma splash screen.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LeafLogo(size = 170.dp)
    }
}
