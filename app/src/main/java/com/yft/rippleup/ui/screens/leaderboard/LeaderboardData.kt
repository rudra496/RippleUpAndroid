package com.yft.rippleup.ui.screens.leaderboard

/**
 * Static leaderboard data — lifted verbatim from index.html so the rankings
 * match the web platform exactly. The user's own team (Youth for Tomorrow) is
 * flagged for highlight.
 */
data class TeamRow(
    val rank: Int,
    val name: String,
    val actions: Int,
    val co2Kg: Double,
    val points: Int,
    val isMine: Boolean = false,
)

data class IndividualRow(
    val rank: Int,
    val name: String,
    val role: String,
    val actions: Int,
    val co2Kg: Double,
    val points: Int,
    val linkedin: String? = null,
    val isMine: Boolean = false,
)

object LeaderboardData {
    val teams = listOf(
        TeamRow(1, "Youth for Tomorrow (RippleUp Founders)", 142, 32.4, 2840, isMine = true),
        TeamRow(2, "Green Campus Alliance", 118, 24.8, 2360),
        TeamRow(3, "SustainSUST Cycling Club", 95, 20.5, 1900),
        TeamRow(4, "Eco Warriors Hub", 82, 16.2, 1640),
        TeamRow(5, "Urban Re-Wilders", 64, 12.0, 1280),
    )

    val individuals = listOf(
        IndividualRow(1, "Saara Vishnoi", "Welfare Designer", 56, 12.2, 1120,
            "https://www.linkedin.com/in/saara-vishnoi-115444336/"),
        IndividualRow(2, "Rudra Sarker", "Project Developer", 48, 10.8, 960,
            "https://www.linkedin.com/in/rudrasarker", isMine = true),
        IndividualRow(3, "Suchita Somkuwar", "Tech Team", 42, 8.9, 840,
            "https://www.linkedin.com/in/suchita-somkuwar"),
        IndividualRow(4, "Priyamvada", "Tech Team", 36, 7.6, 720,
            "https://www.linkedin.com/in/priyamvada-chauhan-6570bb334"),
        IndividualRow(5, "Afsara Tasnim", "Stakeholder Survey", 32, 6.8, 640,
            "https://www.linkedin.com/in/afsara-tasnim-/"),
        IndividualRow(6, "Samia Hossain", "Stakeholder Interview", 28, 5.7, 560),
        IndividualRow(7, "Catherine Waweru", "Stakeholder Interview", 24, 4.8, 480,
            "https://www.linkedin.com/in/catherine-waweru-06a12294/"),
        IndividualRow(8, "Jabir Tukur Bakiyawa", "Tech & Research Support", 20, 4.0, 400,
            "https://www.linkedin.com/in/jabir-tukur-bakiyawa"),
        IndividualRow(9, "Nigel Hove", "Eco Activist", 16, 3.2, 320,
            "https://www.linkedin.com/in/nigel-hove-41426a345"),
        IndividualRow(10, "Anastasia Ayvazyan", "Economics & Finance", 12, 2.4, 240),
    )
}
