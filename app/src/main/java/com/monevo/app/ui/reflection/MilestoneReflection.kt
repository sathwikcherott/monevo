package com.monevo.app.ui.reflection

import androidx.compose.runtime.Immutable

/**
 * Represents a thoughtful reflection on the user's progress.
 */
@Immutable
data class MilestoneReflection(
    val title: String,
    val message: String
)

object MilestoneReflectionGenerator {

    fun generateMilestoneReflection(
        progress: Float,
        daysTracked: Int,
        depositsCount: Int,
        milestonesCompleted: Int
    ): MilestoneReflection {
        
        return when {
            // Case: Final Goal Completed
            progress >= 1f -> {
                MilestoneReflection(
                    title = "A Journey Concluded",
                    message = "A goal completed through discipline. You stayed consistent across $daysTracked days and $depositsCount intentional actions."
                )
            }
            
            // Case: Major Milestone Thresholds
            progress >= 0.75f -> {
                MilestoneReflection(
                    title = "The Habit is Formed",
                    message = "You've crossed $milestonesCompleted milestones. The focus you've built over $daysTracked days is now stronger than the initial motivation."
                )
            }
            
            progress >= 0.50f -> {
                MilestoneReflection(
                    title = "Grounded Progress",
                    message = "Halfway to your goal. $depositsCount steady deposits have shaped this journey. Your consistency is becoming your greatest asset."
                )
            }
            
            progress >= 0.25f -> {
                MilestoneReflection(
                    title = "Established Rhythm",
                    message = "You have maintained this path for $daysTracked days. $milestonesCompleted milestones reached through steady, intentional effort."
                )
            }
            
            // Case: Early Progress
            depositsCount >= 10 -> {
                MilestoneReflection(
                    title = "First Layer Built",
                    message = "10 deposits made. You are proving to yourself that consistency is possible. The foundation is set."
                )
            }
            
            else -> {
                MilestoneReflection(
                    title = "The First Steps",
                    message = "Your journey is underway. Every deposit is an act of discipline. Keep moving forward."
                )
            }
        }
    }
}
