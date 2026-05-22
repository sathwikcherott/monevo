package com.monevo.app.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.monevo.app.model.SavingsTile
import kotlin.random.Random

class SavingsViewModel : ViewModel() {
    val goalAmount: Int = 50000
    private val milestoneStep = 5000
    
    val tiles = mutableStateListOf<SavingsTile>().apply {
        addAll(generateTiles(goalAmount))
    }

    val totalSaved by derivedStateOf {
        tiles.filter { it.isCompleted }.sumOf { it.amount }
    }

    val progress by derivedStateOf {
        totalSaved.toFloat() / goalAmount
    }

    val groupedTiles by derivedStateOf {
        val groups = mutableListOf<MilestoneGroup>()
        var currentGroupTiles = mutableListOf<SavingsTile>()
        var currentGroupSum = 0
        var groupStartValue = 0

        tiles.forEach { tile ->
            currentGroupTiles.add(tile)
            currentGroupSum += tile.amount
            
            if (currentGroupSum >= milestoneStep && groupStartValue + milestoneStep <= goalAmount) {
                groups.add(
                    MilestoneGroup(
                        name = "₹${groupStartValue / 1000}K → ₹${(groupStartValue + milestoneStep) / 1000}K",
                        tiles = currentGroupTiles
                    )
                )
                groupStartValue += milestoneStep
                currentGroupTiles = mutableListOf()
                currentGroupSum = 0
            }
        }
        
        if (currentGroupTiles.isNotEmpty()) {
            groups.add(
                MilestoneGroup(
                    name = "Final Milestone",
                    tiles = currentGroupTiles
                )
            )
        }
        groups
    }

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
            val possibleDenominations = denominations.filter { it <= remaining }
            
            val amount = if (possibleDenominations.isNotEmpty()) {
                possibleDenominations[Random.nextInt(possibleDenominations.size)]
            } else {
                remaining 
            }
            
            result.add(SavingsTile(idCounter++, amount))
            currentSum += amount
        }
        
        return result.shuffled()
    }
}

data class MilestoneGroup(
    val name: String,
    val tiles: List<SavingsTile>
)
