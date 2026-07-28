package com.yft.rippleup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yft.rippleup.data.model.Badge

/** Circular badge chip — coloured when unlocked, dimmed + locked icon otherwise. */
@Composable
fun BadgeChip(
    badge: Badge,
    unlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (unlocked) badge.color.copy(alpha = 0.22f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ),
            contentAlignment = Alignment.Center,
        ) {
            val icon = if (unlocked) ecoIcon(badge.iconName) else ecoIcon("Lock")
            Icon(
                imageVector = icon,
                contentDescription = badge.label,
                tint = if (unlocked) badge.color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp).alpha(if (unlocked) 1f else 0.5f),
            )
        }
        Spacer(Modifier.size(6.dp))
        Text(
            text = badge.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (unlocked) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
