package com.yft.rippleup.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Nutrition
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Resolves the string icon names stored in the model layer to Material
 * ImageVectors. Keeping the mapping in one place means the data layer stays
 * free of Android/Compose dependencies.
 */
@Composable
fun ecoIcon(name: String): ImageVector = when (name) {
    "WaterDrop" -> Icons.Outlined.WaterDrop
    "Nutrition" -> Icons.Outlined.Nutrition
    "Recycling" -> Icons.Outlined.Recycling
    "DirectionsBike" -> Icons.Outlined.DirectionsBike
    "WorkspacePremium" -> Icons.Outlined.WorkspacePremium
    "MilitaryTech" -> Icons.Outlined.MilitaryTech
    "EmojiEvents" -> Icons.Outlined.EmojiEvents
    "Lock" -> Icons.Outlined.Lock
    else -> Icons.Outlined.Recycling
}
