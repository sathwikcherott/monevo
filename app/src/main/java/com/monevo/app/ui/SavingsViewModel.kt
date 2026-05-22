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
import java.util.*
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
    var isOnboardingCompleted by mutableStateOf(true)

    val totalSaved by derivedStateOf {
        tiles.filter { it.isCompleted }.sumOf { it.amount }
    }

    val progress by derivedStateOf {
        totalSaved.toFloat() / goalAmount
    }

    // --- REAL ACTIVITY DATA LOGIC ---

    val weeklyMomentum by derivedStateOf {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        
        // Array for Mon-Sun (0 to 6)
        val daySavings = FloatArray(7) { 0f }
        
        tiles.filter { it.isCompleted && it.completedAt != null }.forEach { tile ->
            calendar.timeInMillis = tile.completedAt!!
            if (calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek && 
                calendar.get(Calendar.YEAR) == currentYear) {
                
                // Adjust day of week (Calendar.SUNDAY is 1, Monday is 2...)
                val day = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> 0
                    Calendar.TUESDAY -> 1
                    Calendar.WEDNESDAY -> 2
                    Calendar.THURSDAY -> 3
                    Calendar.FRIDAY -> 4
                    Calendar.SATURDAY -> 5
                    Calendar.SUNDAY -> 6
                    else -> 0
                }
                daySavings[day] += tile.amount.toFloat()
            }
        }
        
        // Normalize heights (max bar will be around 1.0f)
        val maxAmount = daySavings.maxOrNull() ?: 1f
        daySavings.map { if (maxAmount > 0) it / maxAmount else 0f }
    }

    val consistencyStats by derivedStateOf {
        val completedTiles = tiles.filter { it.isCompleted && it.completedAt != null }
        if (completedTiles.isEmpty()) return@derivedStateOf ConsistencyData(0, 0, 0)
        
        val calendar = Calendar.getInstance()
        
        // Map of "Year:Week" to total savings
        val weeklySavings = mutableMapOf<String, Int>()
        completedTiles.forEach { tile ->
            calendar.timeInMillis = tile.completedAt!!
            val key = "${calendar.get(Calendar.YEAR)}:${calendar.get(Calendar.WEEK_OF_YEAR)}"
            weeklySavings[key] = (weeklySavings[key] ?: 0) + tile.amount
        }
        
        val bestWeek = weeklySavings.values.maxOrNull() ?: 0
        val avgWeekly = if (weeklySavings.isNotEmpty()) weeklySavings.values.average().toInt() else 0
        
        // Streak calculation (consecutive weeks ending with current week)
        var streak = 0
        calendar.timeInMillis = System.currentTimeMillis()
        var currentYear = calendar.get(Calendar.YEAR)
        var currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        
        while (weeklySavings.containsKey("$currentYear:$currentWeek")) {
            streak++
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            currentYear = calendar.get(Calendar.YEAR)
            currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        }
        
        ConsistencyData(streak, bestWeek, avgWeekly)
    }

    // --- MILESTONE LOGIC ---

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
            groups.add(MilestoneGroup(name = "Final Milestone", tiles = currentGroupTiles))
        }

        groups.mapIndexed { index, group ->
            group.copy(isLocked = index >= unlockedMilestoneCount)
        }
    }

    init {
        viewModelScope.launch {
            val savedTilesData = dataStore.completedTilesData.first()
            val savedUnlockedCount = dataStore.unlockedMilestoneCount.first()
            val savedOnboardingStatus = dataStore.isOnboardingCompleted.first()
            
            unlockedMilestoneCount = savedUnlockedCount
            isOnboardingCompleted = savedOnboardingStatus
            
            // Restore tile completion state and timestamps
            savedTilesData.forEach { (id, timestamp) ->
                val index = tiles.indexOfFirst { it.id == id }
                if (index != -1) {
                    tiles[index] = tiles[index].copy(isCompleted = true, completedAt = timestamp)
                }
            }
        }
    }

    fun toggleTile(id: Int) {
        val index = tiles.indexOfFirst { it.id == id }
        if (index != -1) {
            val wasCompleted = tiles[index].isCompleted
            val nowCompleted = !wasCompleted
            
            tiles[index] = tiles[index].copy(
                isCompleted = nowCompleted,
                completedAt = if (nowCompleted) System.currentTimeMillis() else null
            )
            
            if (nowCompleted) {
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
            val completedData = tiles
                .filter { it.isCompleted && it.completedAt != null }
                .associate { it.id to it.completedAt!! }
            dataStore.saveProgress(completedData, unlockedMilestoneCount)
        }
    }

    private fun generateTiles(target: Int): List<SavingsTile> {
        val result = mutableListOf<SavingsTile>()
        val denominations = listOf(50, 100, 150, 200, 300, 500)
        var currentSum = 0
        var idCounter = 0
        val random = Random(42)

        while (currentSum < target) {
            val remaining = target - currentSum
            val possibleDenominations = denominations.filter { it <= remaining }
            val amount = if (possibleDenominations.isNotEmpty()) {
                possibleDenominations[random.nextInt(possibleDenominations.size)]
            } else { remaining }
            
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

data class ConsistencyData(
    val streak: Int,
    val bestWeek: Int,
    val avgWeekly: Int
)
