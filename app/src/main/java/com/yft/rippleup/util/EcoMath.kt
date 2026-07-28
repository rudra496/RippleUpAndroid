package com.yft.rippleup.util

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Emerald
import com.yft.rippleup.ui.theme.EmeraldLight
import com.yft.rippleup.ui.theme.Gold
import com.yft.rippleup.ui.theme.Purple

/**
 * Pure maths for the impact calculator — ported line-for-line from updateCalculator()
 * in the web app.js.
 *
 *   co2     = refills*0.20 + recycles*0.15 + commutes*0.50 + foods*0.30
 *   plastic = refills + recycles
 *   waste   = foods * 0.50
 *   points  = refills*20 + recycles*30 + commutes*25 + foods*15
 *
 * Annualised by * 52 (weeks per year).
 */
data class CalcInputs(
    val refills: Int,
    val recycles: Int,
    val commutesKm: Int,
    val foods: Int,
)

data class CalcResult(
    val co2PerWeek: Double,
    val plasticPerWeek: Int,
    val wastePerWeek: Double,
    val points: Int,
    val annualCo2Kg: Double,
    val annualPlastic: Int,
    val tier: EcoTier,
) {
    val gaugeProgress: Float get() = (points / 1600f).coerceIn(0f, 1f)
}

object EcoMath {

    fun compute(input: CalcInputs): CalcResult {
        val co2 = input.refills * 0.20 + input.recycles * 0.15 +
            input.commutesKm * 0.50 + input.foods * 0.30
        val plastic = input.refills + input.recycles
        val waste = input.foods * 0.50
        val points = input.refills * 20 + input.recycles * 30 +
            input.commutesKm * 25 + input.foods * 15

        return CalcResult(
            co2PerWeek = co2,
            plasticPerWeek = plastic,
            wastePerWeek = waste,
            points = points,
            annualCo2Kg = co2 * 52,
            annualPlastic = plastic * 52,
            tier = EcoTier.forPoints(points),
        )
    }
}

/**
 * Tier ladder from the SVG gauge logic in app.js:
 *   <300  -> Eco Scout      (green)
 *   300+  -> Carbon Champion (mint)
 *   700+  -> Planet Guardian (gold)
 *   1200+ -> Eco Legend      (purple)
 */
enum class EcoTier(
    val title: String,
    val color: Color,
    val threshold: Int,
) {
    Scout("Eco Scout", Emerald, 0),
    Champion("Carbon Champion", EmeraldLight, 300),
    Guardian("Planet Guardian", Gold, 700),
    Legend("Eco Legend", Purple, 1200);

    companion object {
        fun forPoints(points: Int): EcoTier =
            entries.last { points >= it.threshold }
    }
}
