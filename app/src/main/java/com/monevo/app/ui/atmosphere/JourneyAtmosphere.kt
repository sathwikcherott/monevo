package com.monevo.app.ui.atmosphere

import androidx.compose.ui.graphics.Color
import com.monevo.app.ui.theme.PrimaryAccentPink
import com.monevo.app.ui.theme.SoftAccentPink

/**
 * Defines the premium AMOLED-optimized emotional atmosphere stages.
 * Illumination is extremely restrained to maintain deep cinematic darkness.
 */
sealed class JourneyAtmosphere(
    val title: String,
    val supportingText: String,
    val glowIntensity: Float,
    val accentWarmth: Float, // 0f to 1f factor for accent richness
) {
    object FreshStart : JourneyAtmosphere(
        title = "Fresh Start",
        supportingText = "Your savings journey starts here.",
        glowIntensity = 0.12f, // Further reduced for deep AMOLED blacks
        accentWarmth = 0.4f
    )

    object BuildingMomentum : JourneyAtmosphere(
        title = "Building Momentum",
        supportingText = "Consistency is building momentum.",
        glowIntensity = 0.18f,
        accentWarmth = 0.5f
    )

    object FindingConsistency : JourneyAtmosphere(
        title = "Midway Journey",
        supportingText = "Consistency is shaping the outcome.",
        glowIntensity = 0.25f,
        accentWarmth = 0.65f
    )

    object FinalStretch : JourneyAtmosphere(
        title = "Almost There",
        supportingText = "The finish line is getting closer.",
        glowIntensity = 0.45f,
        accentWarmth = 0.9f
    )

    object JourneyComplete : JourneyAtmosphere(
        title = "Goal Achieved",
        supportingText = "A goal completed through discipline.",
        glowIntensity = 0.6f, // Capped to avoid excessive bloom on AMOLED
        accentWarmth = 1.0f
    )

    companion object {
        fun fromProgress(progress: Float): JourneyAtmosphere {
            return when {
                progress >= 1f -> JourneyComplete
                progress >= 0.75f -> FinalStretch
                progress >= 0.25f -> FindingConsistency
                progress > 0f -> BuildingMomentum
                else -> FreshStart
            }
        }
    }
}

/**
 * Extension to get a dynamic color based on atmosphere warmth.
 */
fun JourneyAtmosphere.getAdaptiveAccent(baseColor: Color = SoftAccentPink): Color {
    return Color(
        red = baseColor.red + (PrimaryAccentPink.red - baseColor.red) * accentWarmth,
        green = baseColor.green + (PrimaryAccentPink.green - baseColor.green) * accentWarmth,
        blue = baseColor.blue + (PrimaryAccentPink.blue - baseColor.blue) * accentWarmth,
        alpha = baseColor.alpha
    )
}

fun JourneyAtmosphere.getAdaptiveGold(baseColor: Color = SoftAccentPink): Color = getAdaptiveAccent(baseColor)
