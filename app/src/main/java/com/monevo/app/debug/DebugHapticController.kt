package com.monevo.app.debug

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * [DEBUG ONLY] Controller to manage debug-only haptic visual confirmations.
 */
object DebugHapticController {
    private val _hapticEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val hapticEvents = _hapticEvents.asSharedFlow()

    /**
     * Triggers a visual haptic confirmation event.
     */
    fun triggerVisualHaptic() {
        _hapticEvents.tryEmit(Unit)
    }
}
