package com.monevo.app.debug

import com.monevo.app.ui.SavingsViewModel

/**
 * [DEBUG ONLY] Helper to handle app data reset.
 */
object DebugResetController {

    /**
     * Resets all app data and returns to a clean state.
     */
    fun resetAppData(viewModel: SavingsViewModel) {
        viewModel.resetProgress()
    }
}
