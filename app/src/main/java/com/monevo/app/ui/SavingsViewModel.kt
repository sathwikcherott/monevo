package com.monevo.app.ui

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SavingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = MonevoDataStore(application)
    
    var goalAmount by mutableStateOf(MonevoConfig.DEFAULT_SAVINGS_GOAL)
        private set
    
    private val milestoneStep = 5000
    
    val tiles = mutableStateListOf<SavingsTile>()

    var isOnboardingCompleted by mutableStateOf(true)

    var isHapticsEnabled by mutableStateOf(true)
    var isReducedMotionEnabled by mutableStateOf(false)

    private val shownCelebrationIds = mutableSetOf<String>()
    var activeCelebration by mutableStateOf<CelebrationType?>(null)
        private set

    val totalSaved by derivedStateOf {
        tiles.filter { it.isCompleted }.sumOf { it.amount }
    }

    val progress by derivedStateOf {
        if (goalAmount > 0) totalSaved.toFloat() / goalAmount else 0f
    }

    // --- PURE CALCULATION LOGIC ---

    private fun calculateUnlockedGroups(totalSaved: Int, milestoneStep: Int = 5000): Int {
        return (totalSaved / milestoneStep) + 1
    }

    // --- REAL ACTIVITY DATA LOGIC ---

    val weeklyMomentum by derivedStateOf {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        val daySavings = FloatArray(7) { 0f }
        
        tiles.filter { it.isCompleted && it.completedAt != null }.forEach { tile ->
            calendar.timeInMillis = tile.completedAt!!
            if (calendar.get(Calendar.WEEK_OF_YEAR) == currentWeek && 
                calendar.get(Calendar.YEAR) == currentYear) {
                
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
        val maxAmount = daySavings.maxOrNull() ?: 1f
        daySavings.map { if (maxAmount > 0) it / maxAmount else 0f }
    }

    val consistencyStats by derivedStateOf {
        val completedTiles = tiles.filter { it.isCompleted && it.completedAt != null }
            .sortedBy { it.completedAt }
            
        if (completedTiles.isEmpty()) return@derivedStateOf ConsistencyData(0, 0, 0)
        
        val calendar = Calendar.getInstance()
        
        // 1. Daily Streak
        val completedDays = completedTiles.map {
            calendar.timeInMillis = it.completedAt!!
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }.toSet()
        
        var streak = 0
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        var checkDay = calendar.timeInMillis
        
        // Allow streak to continue if the last save was yesterday or today
        if (!completedDays.contains(checkDay)) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDay = calendar.timeInMillis
        }
        
        while (completedDays.contains(checkDay)) {
            streak++
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            checkDay = calendar.timeInMillis
        }

        // 2. Best Week (Rolling 7-day)
        var bestWeek = 0
        if (completedTiles.isNotEmpty()) {
            completedTiles.forEach { startTile ->
                val startTimestamp = startTile.completedAt!!
                val endTimestamp = startTimestamp + TimeUnit.DAYS.toMillis(7)
                val weekTotal = completedTiles.filter { 
                    it.completedAt in startTimestamp until endTimestamp 
                }.sumOf { it.amount }
                if (weekTotal > bestWeek) bestWeek = weekTotal
            }
        }

        // 3. Average Daily Saving
        val uniqueActiveDays = completedDays.size
        val avgDaily = if (uniqueActiveDays > 0) totalSaved / uniqueActiveDays else 0
        
        ConsistencyData(streak, bestWeek, avgDaily)
    }

    // --- DETERMINISTIC MILESTONE LOGIC ---

    val groupedTiles by derivedStateOf {
        val groups = mutableListOf<MilestoneGroup>()
        var currentGroupTiles = mutableListOf<SavingsTile>()
        var currentGroupSum = 0
        var groupStartValue = 0
        var groupId = 0

        tiles.forEach { tile ->
            currentGroupTiles.add(tile)
            currentGroupSum += tile.amount
            
            if (currentGroupSum >= milestoneStep) {
                groups.add(
                    MilestoneGroup(
                        id = groupId++,
                        rangeStart = groupStartValue,
                        rangeEnd = groupStartValue + currentGroupSum,
                        tiles = currentGroupTiles,
                        isLocked = false,
                        totalGoal = goalAmount
                    )
                )
                groupStartValue += currentGroupSum
                currentGroupTiles = mutableListOf()
                currentGroupSum = 0
            }
        }
        
        if (currentGroupTiles.isNotEmpty()) {
            groups.add(
                MilestoneGroup(
                    id = groupId++,
                    rangeStart = groupStartValue,
                    rangeEnd = goalAmount,
                    tiles = currentGroupTiles,
                    isLocked = false,
                    totalGoal = goalAmount
                )
            )
        }

        val unlockedCount = calculateUnlockedGroups(totalSaved, milestoneStep)
        groups.mapIndexed { index, group ->
            group.copy(isLocked = index >= unlockedCount)
        }
    }

    init {
        viewModelScope.launch {
            val savedGoal = dataStore.goalAmount.first() ?: MonevoConfig.DEFAULT_SAVINGS_GOAL
            goalAmount = savedGoal
            
            tiles.addAll(generateTiles(goalAmount))

            val savedTilesData = dataStore.completedTilesData.first()
            val savedOnboardingStatus = dataStore.isOnboardingCompleted.first()
            val savedCelebrations = dataStore.shownCelebrationIds.first()
            val savedHaptics = dataStore.hapticsEnabled.first()
            val savedReducedMotion = dataStore.reducedMotion.first()
            
            isOnboardingCompleted = savedOnboardingStatus
            shownCelebrationIds.addAll(savedCelebrations)
            isHapticsEnabled = savedHaptics
            isReducedMotionEnabled = savedReducedMotion
            
            savedTilesData.forEach { (id, timestamp) ->
                val index = tiles.indexOfFirst { it.id == id }
                if (index != -1) {
                    tiles[index] = tiles[index].copy(isCompleted = true, completedAt = timestamp)
                }
            }
        }
    }

    fun updateGoal(newGoal: Int) {
        if (newGoal == goalAmount || newGoal <= 0) return
        
        viewModelScope.launch {
            val currentSaved = totalSaved
            dataStore.saveGoalAmount(newGoal)
            goalAmount = newGoal
            
            // Regenerate tiles
            val newTiles = generateTiles(newGoal)
            tiles.clear()
            tiles.addAll(newTiles)
            
            // Restore progress
            var restoredAmount = 0
            tiles.forEachIndexed { index, tile ->
                if (restoredAmount + tile.amount <= currentSaved) {
                    tiles[index] = tile.copy(isCompleted = true, completedAt = System.currentTimeMillis())
                    restoredAmount += tile.amount
                }
            }
            saveState()
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
                checkForCelebration()
            }
            saveState()
        }
    }

    private fun checkForCelebration() {
        val groups = groupedTiles
        // Check standard milestones
        groups.forEach { group ->
            val milestoneId = "milestone_${group.rangeEnd}"
            if (group.isCompleted && !shownCelebrationIds.contains(milestoneId)) {
                triggerCelebration(milestoneId, group.rangeEnd)
            }
        }
        
        // Check final milestone specifically
        if (totalSaved >= goalAmount && !shownCelebrationIds.contains("final_goal")) {
            triggerCelebration("final_goal", goalAmount, isFinal = true)
        }
    }

    private fun triggerCelebration(id: String, amount: Int, isFinal: Boolean = false) {
        shownCelebrationIds.add(id)
        viewModelScope.launch {
            dataStore.markCelebrationShown(id)
            activeCelebration = if (isFinal) {
                CelebrationType.FinalGoal(amount)
            } else {
                CelebrationType.MilestoneReached(amount)
            }
        }
    }

    fun dismissCelebration() {
        activeCelebration = null
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            dataStore.saveOnboardingCompleted()
            isOnboardingCompleted = true
        }
    }

    fun replayOnboarding() {
        isOnboardingCompleted = false
    }

    fun updateHapticsEnabled(enabled: Boolean) {
        isHapticsEnabled = enabled
        viewModelScope.launch { dataStore.saveHapticsEnabled(enabled) }
    }

    fun updateReducedMotion(enabled: Boolean) {
        isReducedMotionEnabled = enabled
        viewModelScope.launch { dataStore.saveReducedMotion(enabled) }
    }

    fun resetProgress() {
        viewModelScope.launch {
            dataStore.clearAll()
            tiles.forEachIndexed { index, tile ->
                tiles[index] = tile.copy(isCompleted = false, completedAt = null)
            }
            isOnboardingCompleted = false
            shownCelebrationIds.clear()
            activeCelebration = null
        }
    }

    private fun saveState() {
        viewModelScope.launch {
            val completedData = tiles
                .filter { it.isCompleted && it.completedAt != null }
                .associate { it.id to it.completedAt!! }
            dataStore.saveProgress(completedData)
        }
    }

    private fun generateTiles(target: Int): List<SavingsTile> {
        val result = mutableListOf<SavingsTile>()
        val milestoneStep = 5000
        var idCounter = 0
        val random = Random(42)

        for (start in 0 until target step milestoneStep) {
            val sectionTarget = minOf(milestoneStep, target - start)
            val sectionTiles = mutableListOf<SavingsTile>()
            var sectionSum = 0
            val denominations = listOf(50, 100, 150, 200, 300, 500)

            while (sectionSum < sectionTarget) {
                val remaining = sectionTarget - sectionSum
                val possibleDenominations = denominations.filter { it <= remaining }
                val amount = if (possibleDenominations.isNotEmpty()) {
                    possibleDenominations[random.nextInt(possibleDenominations.size)]
                } else { remaining }
                
                sectionTiles.add(SavingsTile(idCounter++, amount))
                sectionSum += amount
            }
            result.addAll(sectionTiles.shuffled(random))
        }
        
        return result
    }
}

data class MilestoneGroup(
    val id: Int,
    val rangeStart: Int,
    val rangeEnd: Int,
    val tiles: List<SavingsTile>,
    val isLocked: Boolean,
    val totalGoal: Int
) {
    val name: String
        get() = if (rangeEnd >= totalGoal && rangeStart >= (totalGoal - 5000)) "Final Milestone"
                else "₹${rangeStart / 1000}K → ₹${rangeEnd / 1000}K"

    val isCompleted: Boolean
        get() = tiles.all { it.isCompleted }
}

data class ConsistencyData(
    val streak: Int,
    val bestWeek: Int,
    val avgDaily: Int
)

sealed class CelebrationType {
    abstract val amount: Int
    data class MilestoneReached(override val amount: Int) : CelebrationType()
    data class FinalGoal(override val amount: Int) : CelebrationType()
}
