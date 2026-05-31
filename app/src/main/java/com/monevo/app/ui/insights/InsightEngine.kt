package com.monevo.app.ui.insights

import androidx.compose.runtime.Immutable
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Data model for behavioral insights.
 */
@Immutable
data class InsightData(
    val averageDailySavings: Float,
    val averageWeeklySavings: Float,
    val estimatedDaysRemaining: Int?,
    val depositsPerWeek: Float,
    val longestStreak: Int,
    val milestonesCompleted: Int,
    val reflectionInsight: String
)

object InsightEngine {

    fun calculateInsights(
        totalSaved: Int,
        goalAmount: Int,
        progress: Float,
        daysTracked: Int,
        completedTilesCount: Int,
        milestonesCompleted: Int,
        currentStreak: Int,
        completionTimestamps: List<Long>
    ): InsightData {
        
        // 1. Pace Calculations
        val safeDaysTracked = daysTracked.coerceAtLeast(1)
        val avgDaily = totalSaved.toFloat() / safeDaysTracked
        val avgWeekly = avgDaily * 7

        // 2. Consistency Metrics
        val depositsPerWeek = completedTilesCount.toFloat() / (safeDaysTracked / 7f).coerceAtLeast(1f)
        val longestStreak = calculateLongestStreak(completionTimestamps, currentStreak)

        // 3. Forecast Logic
        val remainingAmount = goalAmount - totalSaved
        val estimatedDaysRemaining = if (progress >= 1f) {
            0
        } else if (avgDaily > 0 && daysTracked >= 3) {
            (remainingAmount / avgDaily).roundToInt()
        } else {
            null
        }

        // 4. Dynamic Reflection Insight
        val reflection = generateReflection(
            progress = progress,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            depositsCount = completedTilesCount,
            daysTracked = safeDaysTracked,
            avgDaily = avgDaily
        )

        return InsightData(
            averageDailySavings = avgDaily,
            averageWeeklySavings = avgWeekly,
            estimatedDaysRemaining = estimatedDaysRemaining,
            depositsPerWeek = depositsPerWeek,
            longestStreak = longestStreak,
            milestonesCompleted = milestonesCompleted,
            reflectionInsight = reflection
        )
    }

    private fun calculateLongestStreak(timestamps: List<Long>, currentStreak: Int): Int {
        if (timestamps.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        val sortedDays = timestamps.map {
            calendar.timeInMillis = it
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }.distinct().sorted()

        var maxStreak = 0
        var currentRunningStreak = 0
        var lastDay: Long? = null

        for (day in sortedDays) {
            if (lastDay == null) {
                currentRunningStreak = 1
            } else {
                val diff = day - lastDay
                if (diff <= TimeUnit.DAYS.toMillis(1)) {
                    currentRunningStreak++
                } else {
                    maxStreak = maxOf(maxStreak, currentRunningStreak)
                    currentRunningStreak = 1
                }
            }
            lastDay = day
        }
        
        return maxOf(maxOf(maxStreak, currentRunningStreak), currentStreak)
    }

    private fun generateReflection(
        progress: Float,
        currentStreak: Int,
        longestStreak: Int,
        depositsCount: Int,
        daysTracked: Int,
        avgDaily: Float
    ): String {
        return when {
            progress >= 1f -> "A journey concluded through discipline."
            currentStreak >= 5 && currentStreak >= longestStreak -> "You are currently at your peak consistency."
            longestStreak >= 7 -> "Your longest streak of $longestStreak days shows your capacity for discipline."
            progress >= 0.5f -> "The habit you've built is now stronger than the starting motivation."
            depositsCount >= 15 -> "The majority of your progress comes from steady, repeated actions."
            avgDaily > 1000 -> "Your focused pace is significantly advancing your timeline."
            daysTracked >= 14 && depositsCount < 5 -> "Focus on small, regular deposits to build momentum."
            else -> "Your journey is taking shape through intentional steps."
        }
    }
}
