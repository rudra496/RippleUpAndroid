package com.yft.rippleup.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.EmeraldLight

/** A heading word painted with the emerald→mint gradient, like the web hero. */
@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayMedium,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            brush = Brush.linearGradient(listOf(Emerald, EmeraldLight)),
            fontWeight = FontWeight.Bold,
        ),
    )
}

/**
 * Big animated counter — the dashboard points display. Ports animateCounter()
 * from app.js: easeOutQuad tween over ~1.2s. Supports integer and one-decimal
 * float modes (for CO2).
 */
@Composable
fun AnimatedCounter(
    target: Int,
    modifier: Modifier = Modifier,
    durationMs: Int = 1100,
    decimals: Int = 0,
    textSuffix: String = "",
    textPrefix: String = "",
    style: TextStyle = MaterialTheme.typography.displayMedium,
) {
    val animated = remember { Animatable(initialValue = target.toFloat()) }
    LaunchedEffect(target) {
        animated.animateTo(target.toFloat(), tween(durationMillis = durationMs))
    }
    val display = if (decimals == 0) animated.value.toInt().toString()
    else String.format("%.${decimals}f", animated.value)
    Text(
        text = "$textPrefix$display$textSuffix",
        modifier = modifier,
        style = style.copy(fontWeight = FontWeight.ExtraBold),
        textAlign = TextAlign.Center,
    )
}

/** Float overload — used for CO2 values like 7.0 kg. */
@Composable
fun AnimatedCounter(
    target: Double,
    modifier: Modifier = Modifier,
    durationMs: Int = 1100,
    decimals: Int = 1,
    textSuffix: String = "",
    textPrefix: String = "",
    style: TextStyle = MaterialTheme.typography.displayMedium,
) {
    val animated = remember { Animatable(initialValue = target.toFloat()) }
    LaunchedEffect(target) {
        animated.animateTo(target.toFloat(), tween(durationMillis = durationMs))
    }
    val display = String.format("%.${decimals}f", animated.value)
    Text(
        text = "$textPrefix$display$textSuffix",
        modifier = modifier,
        style = style.copy(fontWeight = FontWeight.ExtraBold),
        textAlign = TextAlign.Center,
    )
}
