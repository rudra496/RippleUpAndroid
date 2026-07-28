package com.yft.rippleup.ui.screens.more

import androidx.compose.ui.graphics.Color
import com.yft.rippleup.ui.theme.Sdg11
import com.yft.rippleup.ui.theme.Sdg12
import com.yft.rippleup.ui.theme.Sdg13
import com.yft.rippleup.ui.theme.Sdg15
import com.yft.rippleup.ui.theme.Sdg2
import com.yft.rippleup.ui.theme.Sdg3

/**
 * Static informational content for the "More" hub — transplanted verbatim from
 * the web's index.html so nothing from the original platform is lost.
 */
data class Sdg(val number: Int, val name: String, val color: Color)

object ContentData {
    val sdgs = listOf(
        Sdg(2, "Zero Hunger", Sdg2),
        Sdg(3, "Good Health & Well-being", Sdg3),
        Sdg(11, "Sustainable Cities", Sdg11),
        Sdg(12, "Responsible Consumption", Sdg12),
        Sdg(13, "Climate Action", Sdg13),
        Sdg(15, "Life on Land", Sdg15),
    )

    // The 10-panel user journey from the storyboard section.
    data class JourneyStep(val num: String, val heading: String, val desc: String)
    val journey = listOf(
        JourneyStep("01", "The Awareness Gap",
            "Many young people are aware of climate challenges and urban waste issues but feel overwhelmed and unsure how their individual actions can make a real difference."),
        JourneyStep("02", "Discovering RippleUp",
            "Youth encounter RippleUp posters and digital campaigns across campuses, suggesting a rewarding way to build sustainability into daily life."),
        JourneyStep("03", "First Sustainable Action",
            "Instead of buying a single-use plastic bottle, a student decides to refill their reusable bottle at a designated campus water station."),
        JourneyStep("04", "Action Verification",
            "Scanning the QR code at the water station registers the action instantly. The user sees immediate feedback: +20 points and a started streak!"),
        JourneyStep("05", "Streak & Habit Building",
            "Through gamified elements like a 5-day streak and healthy habit tracking, the daily green action is reinforced, making it natural and motivating."),
        JourneyStep("06", "Supporting Local Businesses",
            "Users earn points and discount vouchers, which they redeem at participating local green vendors, driving sustainable local circular economy."),
        JourneyStep("07", "Expanding Eco Actions",
            "Having built water-refill habits, users begin exploring other actions: returning plastics to recycling bins or participating in group trash collection."),
        JourneyStep("08", "Systemic Habit Shifts",
            "The youth participant makes a habit of carrying cloth bags, refusing disposable packaging, and preferring green mobility options like cycling or walking."),
        JourneyStep("09", "Seeing Collective Impact",
            "The dashboard displays quantified environmental impact. Reviewing metrics of saved CO2 and reduced landfill waste creates a strong feeling of efficacy."),
        JourneyStep("10", "Second Nature",
            "Sustainable living becomes a standard, effortless part of everyday life. Habit-formation is complete, and the youth inspires their peers to join the journey."),
    )

    // Survey bar charts (research pillar 1).
    data class Chart(val label: String, val pct: Float, val danger: Boolean = false, val info: Boolean = false)
    val charts = listOf(
        Chart("Would adopt green habits if rewarded", 77.3f),
        Chart("Identify poor infrastructure as top barrier", 63.6f, danger = true),
        Chart("Prefer gamified digital habit tracking", 70.0f, info = true),
    )

    // Research pillar metadata.
    data class Research(
        val icon: String, val accent: Color, val title: String,
        val desc: String, val extra: String? = null,
    )

    val research = listOf(
        Research("QuestionMark", Color(0xFFEF4444), "1. Our Why: Problem & Audience",
            "Despite rising awareness, sustainable habit loops among urban youth in developing countries remain inconsistent. Intention exists, but reward loops and supporting infrastructure are absent.",
            "Global Toll: 1.05 billion tonnes of annual food waste (8–10% of global emissions) & 20 million metric tonnes of plastic environmental leakage."),
        Research("Lightbulb", Color(0xFFF59E0B), "2. Our What: The Platform Loop",
            "RippleUp gamifies eco-habits without requesting drastic lifestyle shifts. By refilling bottles, recycling, or choosing local produce, users verify actions at physical stations to earn instant feedback.",
            "Build Feasibility: Ready for real-world validation via QR stickers, custom app simulator frontends, and community partnerships."),
        Research("ShowChart", Color(0xFF10B981), "3. Our Impact: Habit Loops",
            "Our qualitative interviews confirmed that youth are significantly more likely to continue green behaviors when they are simple, visually tracked, and integrated.",
            null),
        Research("Route", Color(0xFF3B82F6), "4. Our Journey: Ideate Pivot",
            "We began with a broad goal to address multiple climate challenges. Through mentor feedback, we successfully pivoted to a focused, high-retention platform centered on physical QR codes.",
            null),
    )

    val quotes = listOf(
        "Ridwan, 25:" to "\"Vehicle pollution and outdoor heat are critical. I'd scan a colorful QR code at a water station if it was fun, quick, and tracked my impact.\"",
        "Tanvir, 20:" to "\"Eco living should emerge naturally. Seeing my points bar fill up and unlocking discounts encourages me to come back.\"",
    )

    val statBox = ">70%" to "Express direct preference for digital habit tracking with points."

    // Legal documents — abridged headings; full text shown in the reader screen.
    data class LegalDoc(val key: String, val title: String, val updated: String)
    val legalDocs = listOf(
        LegalDoc("legal", "Legal Notices", "20 July 2026"),
        LegalDoc("privacy", "Personal Data", "20 July 2026"),
        LegalDoc("terms", "Terms of Service", "20 July 2026"),
        LegalDoc("cookies", "Cookies Policy", "20 July 2026"),
    )
}
