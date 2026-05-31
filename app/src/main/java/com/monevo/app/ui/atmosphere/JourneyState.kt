package com.monevo.app.ui.atmosphere

/**
 * Defines the dynamic emotional progression system for the savings journey.
 */
data class JourneyState(
    val title: String,
    val message: String,
    val progressRange: ClosedFloatingPointRange<Float>
)

object JourneyStateProvider {
    val FreshStart = JourneyState(
        title = "Fresh Start",
        message = "Every journey starts somewhere.",
        progressRange = 0f..0f
    )

    val MomentumBuilding = JourneyState(
        title = "Momentum Building",
        message = "Small actions are becoming progress.",
        progressRange = 0.01f..0.25f
    )

    val MidwayJourney = JourneyState(
        title = "Midway Journey",
        message = "Consistency is shaping the outcome.",
        progressRange = 0.25f..0.75f
    )

    val AlmostThere = JourneyState(
        title = "Almost There",
        message = "The finish line is getting closer.",
        progressRange = 0.75f..0.999f
    )

    val GoalAchieved = JourneyState(
        title = "Goal Achieved",
        message = "A goal completed through discipline.",
        progressRange = 1f..1f
    )

    fun getJourneyState(progress: Float): JourneyState {
        return when {
            progress >= 1f -> GoalAchieved
            progress >= 0.75f -> AlmostThere
            progress >= 0.25f -> MidwayJourney
            progress > 0f -> MomentumBuilding
            else -> FreshStart
        }
    }
}
