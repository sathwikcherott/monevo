package com.monevo.app.ui.motion

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Centralized motion settings for the application.
 */
data class MotionSettings(
    val isReducedMotionEnabled: Boolean = false
) {
    // --- Animation Scaling Helpers ---

    /**
     * Scale a value based on reduced motion state.
     */
    fun scaleValue(normal: Float, reduced: Float): Float = if (isReducedMotionEnabled) reduced else normal

    /**
     * Scale a duration.
     */
    fun scaleDuration(millis: Int, factor: Float = 1.2f): Int = 
        if (isReducedMotionEnabled) (millis * factor).toInt() else millis

    /**
     * Reusable spring specs.
     */
    fun <T> gentleSpring() = spring<T>(
        dampingRatio = if (isReducedMotionEnabled) Spring.DampingRatioNoBouncy else Spring.DampingRatioNoBouncy,
        stiffness = if (isReducedMotionEnabled) Spring.StiffnessVeryLow else Spring.StiffnessMedium
    )

    /**
     * Scale Dp values (e.g. elevation).
     */
    fun scaleDp(normal: Dp, reduced: Dp): Dp = if (isReducedMotionEnabled) reduced else normal
}

val LocalMotionSettings = staticCompositionLocalOf { MotionSettings() }

@Composable
fun ProvideMotionSettings(isReducedMotionEnabled: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMotionSettings provides MotionSettings(isReducedMotionEnabled)) {
        content()
    }
}
