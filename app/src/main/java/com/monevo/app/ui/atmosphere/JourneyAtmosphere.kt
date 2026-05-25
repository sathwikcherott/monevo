package com.monevo.app.ui.atmosphere

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.monevo.app.ui.theme.AccentGold
import com.monevo.app.ui.theme.SoftGold

/**
 * Defines the emotional atmosphere stages of the savings journey.
 */
sealed class JourneyAtmosphere(
    val title: String,
    val supportingText: String,
    val glowIntensity: Float,
    val accentWarmth: Float, // 0f to 1f factor for gold richness
) {
    object FreshStart : JourneyAtmosphere(
        title = "Fresh Start",
        supportingText = "Your savings journey starts here.",
        glowIntensity = 0.4f,
        accentWarmth = 0.5f
    )

    object BuildingMomentum : JourneyAtmosphere(
        title = "Building Momentum",
        supportingText = "Consistency is building momentum.",
        glowIntensity = 0.5f,
        accentWarmth = 0.6f
    )

    object FindingConsistency : JourneyAtmosphere(
        title = "Finding Consistency",
        supportingText = "Your savings rhythm is taking shape.",
        glowIntensity = 0.6f,
        accentWarmth = 0.7f
    )

    object StrongProgress : JourneyAtmosphere(
        title = "Strong Progress",
        supportingText = "Your discipline is becoming visible.",
        glowIntensity = 0.75f,
        accentWarmth = 0.85f
    )

    object FinalStretch : JourneyAtmosphere(
        title = "Final Stretch",
        supportingText = "The goal is now within reach.",
        glowIntensity = 0.9f,
        accentWarmth = 0.95f
    )

    object JourneyComplete : JourneyAtmosphere(
        title = "Journey Complete",
        supportingText = "A disciplined journey completed.",
        glowIntensity = 1.0f,
        accentWarmth = 1.0f
    )

    companion object {
        fun fromProgress(progress: Float): JourneyAtmosphere {
            return when {
                progress >= 1f -> JourneyComplete
                progress >= 0.85f -> FinalStretch
                progress >= 0.60f -> StrongProgress
                progress >= 0.30f -> FindingConsistency
                progress > 0f -> BuildingMomentum
                else -> FreshStart
            }
        }
    }
}

/**
 * Extension to get a dynamic color based on atmosphere warmth.
 */
fun JourneyAtmosphere.getAdaptiveGold(baseColor: Color = SoftGold): Color {
    // Subtly shift color richness towards AccentGold as warmth increases
    return Color(
        red = baseColor.red + (AccentGold.red - baseColor.red) * accentWarmth,
        green = baseColor.green + (AccentGold.green - baseColor.green) * accentWarmth,
        blue = baseColor.blue + (AccentGold.blue - baseColor.blue) * accentWarmth,
        alpha = baseColor.alpha
    )
}
