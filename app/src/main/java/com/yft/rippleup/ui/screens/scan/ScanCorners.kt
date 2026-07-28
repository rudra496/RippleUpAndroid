package com.yft.rippleup.ui.screens.scan

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yft.rippleup.ui.theme.EmeraldLight

/**
 * Draws four corner brackets around the element, like the web's scan-frame
 * `.corner` spans. Use on a sized Box.
 */
fun Modifier.scanCorners(
    color: Color = EmeraldLight,
    bracket: Dp = 34.dp,
    thickness: Dp = 4.dp,
): Modifier = this.then(
    Modifier.drawBehind {
        val b = bracket.toPx()
        val t = thickness.toPx()
        val w = size.width
        val h = size.height

        // top-left
        drawLine(color, Offset(0f, 0f), Offset(b, 0f), t)
        drawLine(color, Offset(0f, 0f), Offset(0f, b), t)
        // top-right
        drawLine(color, Offset(w - b, 0f), Offset(w, 0f), t)
        drawLine(color, Offset(w, 0f), Offset(w, b), t)
        // bottom-left
        drawLine(color, Offset(0f, h - b), Offset(0f, h), t)
        drawLine(color, Offset(0f, h), Offset(b, h), t)
        // bottom-right
        drawLine(color, Offset(w - b, h), Offset(w, h), t)
        drawLine(color, Offset(w, h - b), Offset(w, h), t)
    },
)
