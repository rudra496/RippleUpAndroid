package com.yft.rippleup.data.model

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Coral
import com.yft.rippleup.ui.theme.EmeraldLight
import com.yft.rippleup.ui.theme.Purple
import com.yft.rippleup.ui.theme.SkyBlue

/**
 * The four core eco-actions. Points and CO2 values are lifted verbatim from the
 * web project's app.js logAction() calls so behaviour is identical.
 *
 *   refill   -> 20 pts, 0.20 kg   ("Campus Refill Station #3")
 *   food     -> 15 pts, 0.30 kg   ("Avoided Food Waste")
 *   recycle  -> 30 pts, 0.15 kg   ("Recycling Hub B")
 *   transit  -> 25 pts, 0.50 kg   ("Walk/Cycling Commute")
 *
 * Three additional "scan" actions exist (the QR presets) — see [ScanPreset].
 */
enum class EcoAction(
    val title: String,
    val points: Int,
    val co2Kg: Double,
    val color: Color,
    val iconName: String, // Material icon name resolved in the UI layer
) {
    Refill(
        title = "Campus Refill Station #3",
        points = 20,
        co2Kg = 0.20,
        color = SkyBlue,
        iconName = "WaterDrop",
    ),
    Food(
        title = "Avoided Food Waste",
        points = 15,
        co2Kg = 0.30,
        color = Coral,
        iconName = "Nutrition",
    ),
    Recycle(
        title = "Recycling Hub B",
        points = 30,
        co2Kg = 0.15,
        color = EmeraldLight,
        iconName = "Recycling",
    ),
    Transit(
        title = "Walk/Cycling Commute",
        points = 25,
        co2Kg = 0.50,
        color = Purple,
        iconName = "DirectionsBike",
    ),
}
