package com.monevo.app.debug

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.monevo.app.BuildConfig

/**
 * [DEBUG ONLY] Intercepts all haptic feedback calls to trigger visual confirmation.
 * Respects the app's global haptics setting.
 */
@Composable
fun DebugHapticInterceptor(isAppHapticsEnabled: Boolean, content: @Composable () -> Unit) {
    if (!BuildConfig.DEBUG) {
        content()
        return
    }

    val originalHaptic = LocalHapticFeedback.current
    
    val wrappedHaptic = object : HapticFeedback {
        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
            // Only perform vibration if enabled in app settings
            if (isAppHapticsEnabled) {
                originalHaptic.performHapticFeedback(hapticFeedbackType)
            }
            
            // Notify visualizer (logic inside controller checks if viz mode is ON)
            DebugHapticController.onHapticExecuted(isAppHapticsEnabled)
        }
    }
    
    CompositionLocalProvider(LocalHapticFeedback provides wrappedHaptic) {
        content()
    }
}
