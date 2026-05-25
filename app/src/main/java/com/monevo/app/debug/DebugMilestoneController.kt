package com.monevo.app.debug

import com.monevo.app.ui.SavingsViewModel

/**
 * [DEBUG ONLY] Helper to simulate milestone progression.
 * This file is temporary for testing purposes.
 */
object DebugMilestoneController {

    /**
     * Completes one milestone section sequentially.
     */
    fun advanceMilestone(viewModel: SavingsViewModel) {
        val groups = viewModel.groupedTiles
        // Find the first group that isn't fully completed
        val nextGroup = groups.firstOrNull { !it.isCompleted }
        
        nextGroup?.let { group ->
            group.tiles.forEach { tile ->
                if (!tile.isCompleted) {
                    // Update in-memory state only to avoid permanent data alteration 
                    // unless viewModel logic is changed to save explicitly.
                    val index = viewModel.tiles.indexOfFirst { it.id == tile.id }
                    if (index != -1) {
                        viewModel.tiles[index] = viewModel.tiles[index].copy(
                            isCompleted = true,
                            completedAt = System.currentTimeMillis()
                        )
                    }
                }
            }
        }
    }

    /**
     * Reverses the milestone progression one section at a time.
     */
    fun reverseMilestone(viewModel: SavingsViewModel) {
        val groups = viewModel.groupedTiles
        // Find the last group that has any completed tiles
        val lastCompletedGroup = groups.lastOrNull { group -> 
            group.tiles.any { it.isCompleted } 
        }
        
        lastCompletedGroup?.let { group ->
            group.tiles.forEach { tile ->
                if (tile.isCompleted) {
                    val index = viewModel.tiles.indexOfFirst { it.id == tile.id }
                    if (index != -1) {
                        viewModel.tiles[index] = viewModel.tiles[index].copy(
                            isCompleted = false,
                            completedAt = null
                        )
                    }
                }
            }
        }
    }
}
