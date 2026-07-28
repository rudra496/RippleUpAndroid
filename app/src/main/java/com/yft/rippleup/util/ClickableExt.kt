package com.yft.rippleup.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Ripple-free clickable for small icon taps (LinkedIn links) where the standard
 * ripple looks noisy. Compose 2024's `clickable` has an `indication` param that
 * we null out.
 */
@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
        indication = null,
        onClick = onClick,
    ),
)
