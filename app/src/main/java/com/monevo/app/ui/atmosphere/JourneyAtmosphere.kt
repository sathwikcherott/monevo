package com.monevo.app.ui.atmosphere

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.PrimaryAccentPink
import com.monevo.app.ui.theme.SoftAccentPink

/**
 * Defines the premium AMOLED-optimized emotional atmosphere stages.
 * Illumination is extremely restrained to maintain deep cinematic darkness.
 */
@Stable
class AtmosphereState(
    val glow: State<Float>,
    val warmth: State<Float>,
    val richness: State<Float>
)

sealed class JourneyAtmosphere(
    val title: String,
    val supportingText: String,
    val glowIntensity: Float,
    val accentWarmth: Float, // 0f to 1f factor for accent richness
    val surfaceRichness: Float, // 0f to 1f factor for surface opacity/emphasis
) {
    object FreshStart : JourneyAtmosphere(
        title = "Fresh Start",
        supportingText = "Every journey starts somewhere.",
        glowIntensity = 0.08f, 
        accentWarmth = 0.1f,
        surfaceRichness = 0.2f
    )

    object BuildingMomentum : JourneyAtmosphere(
        title = "Momentum Building",
        supportingText = "Small actions are becoming progress.",
        glowIntensity = 0.16f,
        accentWarmth = 0.35f,
        surfaceRichness = 0.35f
    )

    object FindingConsistency : JourneyAtmosphere(
        title = "Midway Journey",
        supportingText = "Consistency is shaping the outcome.",
        glowIntensity = 0.24f,
        accentWarmth = 0.6f,
        surfaceRichness = 0.5f
    )

    object FinalStretch : JourneyAtmosphere(
        title = "Almost There",
        supportingText = "The finish line is getting closer.",
        glowIntensity = 0.38f,
        accentWarmth = 0.85f,
        surfaceRichness = 0.75f
    )

    object JourneyComplete : JourneyAtmosphere(
        title = "Goal Achieved",
        supportingText = "A goal completed through discipline.",
        glowIntensity = 0.55f, 
        accentWarmth = 1.0f,
        surfaceRichness = 1.0f
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

@Composable
fun rememberAnimatedAtmosphere(target: JourneyAtmosphere): AtmosphereState {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    
    val duration = if (isReducedMotion) 0 else 3000
    
    val glow = animateFloatAsState(
        targetValue = target.glowIntensity,
        animationSpec = tween(duration),
        label = "glow"
    )
    
    val warmth = animateFloatAsState(
        targetValue = target.accentWarmth,
        animationSpec = tween(duration),
        label = "warmth"
    )
    
    val richness = animateFloatAsState(
        targetValue = target.surfaceRichness,
        animationSpec = tween(duration),
        label = "richness"
    )
    
    return remember {
        AtmosphereState(glow, warmth, richness)
    }
}

/**
 * Extension to get a dynamic color based on atmosphere warmth.
 */
fun getAdaptiveAccent(warmth: Float, baseColor: Color = SoftAccentPink): Color {
    return Color(
        red = baseColor.red + ((PrimaryAccentPink.red - baseColor.red) * warmth),
        green = baseColor.green + ((PrimaryAccentPink.green - baseColor.green) * warmth),
        blue = baseColor.blue + ((PrimaryAccentPink.blue - baseColor.blue) * warmth),
        alpha = baseColor.alpha
    )
}

fun JourneyAtmosphere.getAdaptiveAccent(baseColor: Color = SoftAccentPink): Color = 
    getAdaptiveAccent(accentWarmth, baseColor)

fun JourneyAtmosphere.getAdaptiveGold(baseColor: Color = SoftAccentPink): Color = getAdaptiveAccent(baseColor)
