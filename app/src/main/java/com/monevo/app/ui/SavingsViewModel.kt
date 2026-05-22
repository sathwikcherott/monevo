package com.monevo.app.ui

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.monevo.app.config.MonevoConfig
import com.monevo.app.data.MonevoDataStore
import com.monevo.app.model.SavingsTile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class SavingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = MonevoDataStore(application)
    
    val goalAmount: Int = MonevoConfig.DEFAULT_SAVINGS_GOAL
    private val milestoneStep = MonevoConfig.MILESTONE_STEP
    
    val tiles = mutableStateListOf<SavingsTile>().apply {
        addAll(generateTiles(goalAmount))
    }

    var unlockedMilestoneCount by mutableIntStateOf(2)
    var showUnlockDialog by mutableStateOf(false)
    var isOnboardingCompleted by mutableStateOf(true) // Default to true to avoid flicker before load

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

        groups.mapIndexed { index, group ->
            group.copy(isLocked = index >= unlockedMilestoneCount)
        }
    }

    init {
        viewModelScope.launch {
            val savedCompletedIds = dataStore.completedTileIds.first()
            val savedUnlockedCount = dataStore.unlockedMilestoneCount.first()
            val savedOnboardingStatus = dataStore.isOnboardingCompleted.first()
            
            unlockedMilestoneCount = savedUnlockedCount
            isOnboardingCompleted = savedOnboardingStatus
            
            // Restore tile completion state
            savedCompletedIds.forEach { id ->
                val index = tiles.indexOfFirst { it.id == id }
                if (index != -1) {
                    tiles[index] = tiles[index].copy(isCompleted = true)
                }
            }
        }
    }

    fun toggleTile(id: Int) {
        val index = tiles.indexOfFirst { it.id == id }
        if (index != -1) {
            val wasCompleted = tiles[index].isCompleted
            tiles[index] = tiles[index].copy(isCompleted = !wasCompleted)
            
            // If we just completed a tile and it was the last tile in the latest unlocked group
            if (!wasCompleted) {
                val latestUnlockedIndex = unlockedMilestoneCount - 1
                if (latestUnlockedIndex < groupedTiles.size) {
                    val latestGroup = groupedTiles[latestUnlockedIndex]
                    if (latestGroup.isCompleted) {
                        showUnlockDialog = true
                    }
                }
            }
            saveState()
        }
    }

    fun unlockMilestones(count: Int) {
        unlockedMilestoneCount += count
        showUnlockDialog = false
        saveState()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.saveOnboardingCompleted()
            isOnboardingCompleted = true
        }
    }

    private fun saveState() {
        viewModelScope.launch {
            val completedIds = tiles.filter { it.isCompleted }.map { it.id }.toSet()
            dataStore.saveProgress(completedIds, unlockedMilestoneCount)
        }
    }

    private fun generateTiles(target: Int): List<SavingsTile> {
        val result = mutableListOf<SavingsTile>()
        val denominations = listOf(50, 100, 150, 200, 300, 500)
        var currentSum = 0
        var idCounter = 0
        
        // Use a fixed seed for deterministic tile generation across app restarts
        val random = Random(42)

        while (currentSum < target) {
            val remaining = target - currentSum
            val possibleDenominations = denominations.filter { it <= remaining }
            
            val amount = if (possibleDenominations.isNotEmpty()) {
                possibleDenominations[random.nextInt(possibleDenominations.size)]
            } else {
                remaining 
            }
            
            result.add(SavingsTile(idCounter++, amount))
            currentSum += amount
        }
        
        return result.shuffled(random)
    }
}

data class MilestoneGroup(
    val name: String,
    val tiles: List<SavingsTile>,
    val isLocked: Boolean = false
) {
    val isCompleted: Boolean
        get() = tiles.all { it.isCompleted }
}
