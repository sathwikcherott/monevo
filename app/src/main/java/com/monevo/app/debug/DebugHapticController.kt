package com.monevo.app.debug

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * [DEBUG ONLY] Controller to manage debug-only haptic visual confirmations.
 * The visualizer behaves as a passive observer of valid haptic events.
 */
object DebugHapticController {
    /**
     * Toggles whether haptic events should be visualized with a ripple.
     * This is the "Haptic Viz" mode in the debug menu.
     */
    var isVizModeEnabled by mutableStateOf(false)

    private val _hapticEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val hapticEvents = _hapticEvents.asSharedFlow()

    /**
     * Notifies the visualizer that a haptic event was executed.
     * The visualizer will only trigger if both the debug Viz Mode is active
     * AND the app-level haptics setting is enabled.
     */
    fun onHapticExecuted(isAppHapticsEnabled: Boolean) {
        if (isVizModeEnabled && isAppHapticsEnabled) {
            _hapticEvents.tryEmit(Unit)
        }
    }
}
