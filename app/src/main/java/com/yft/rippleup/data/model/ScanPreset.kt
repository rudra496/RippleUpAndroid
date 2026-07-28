package com.yft.rippleup.data.model

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Coral
import com.yft.rippleup.ui.theme.EmeraldLight
import com.yft.rippleup.ui.theme.SkyBlue

/**
 * The three QR presets from the web's preset-btn scanner section.
 *
 *   campus-refill -> Water Station #4 Scan        25 pts, 0.20 kg
 *   green-grocer  -> Organic Grocer Stall #7 Scan 20 pts, 0.35 kg
 *   recycle-bin   -> Recycling Point #12 Scan     35 pts, 0.15 kg
 */
enum class ScanPreset(
    val id: String,
    val title: String,
    val points: Int,
    val co2Kg: Double,
    val color: Color,
    val iconName: String,
) {
    CampusRefill(
        id = "campus-refill",
        title = "Campus Water Refill #4",
        points = 25,
        co2Kg = 0.20,
        color = SkyBlue,
        iconName = "WaterDrop",
    ),
    GreenGrocer(
        id = "green-grocer",
        title = "Organic Grocer Stall #7",
        points = 20,
        co2Kg = 0.35,
        color = Coral,
        iconName = "Nutrition",
    ),
    RecycleBin(
        id = "recycle-bin",
        title = "Smart Recycling Point #12",
        points = 35,
        co2Kg = 0.15,
        color = EmeraldLight,
        iconName = "Recycling",
    ),
}
