package com.monevo.app.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.monevo.app.model.SavingsTile
import kotlin.random.Random

class SavingsViewModel : ViewModel() {
    val goalAmount: Int = 50000
    
    val tiles = mutableStateListOf<SavingsTile>().apply {
        addAll(generateTiles(goalAmount))
    }

    val totalSaved: Int
        get() = tiles.filter { it.isCompleted }.sumOf { it.amount }

    val progress: Float
        get() = totalSaved.toFloat() / goalAmount

    fun toggleTile(id: Int) {
        val index = tiles.indexOfFirst { it.id == id }
        if (index != -1) {
            tiles[index] = tiles[index].copy(isCompleted = !tiles[index].isCompleted)
        }
    }

    private fun generateTiles(target: Int): List<SavingsTile> {
        val result = mutableListOf<SavingsTile>()
        val denominations = listOf(50, 100, 150, 200, 300, 500)
        var currentSum = 0
        var idCounter = 0

        while (currentSum < target) {
            val remaining = target - currentSum
            // Filter denominations that can fit in the remaining amount
            val possibleDenominations = denominations.filter { it <= remaining }
            
            val amount = if (possibleDenominations.isNotEmpty()) {
                possibleDenominations[Random.nextInt(possibleDenominations.size)]
            } else {
                // This case handles if target isn't a multiple of 50, 
                // but 50,000 is, so it shouldn't happen with these denominations.
                remaining 
            }
            
            result.add(SavingsTile(idCounter++, amount))
            currentSum += amount
        }
        
        // Shuffle to avoid having all small tiles at the end
        return result.shuffled()
    }
}
