package com.monevo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveAccent
import com.monevo.app.ui.insights.InsightData
import com.monevo.app.ui.theme.*

@Composable
fun InsightsCard(
    insightDataProvider: () -> InsightData,
    atmosphereProvider: () -> JourneyAtmosphere,
    modifier: Modifier = Modifier
) {
    val insights = insightDataProvider()
    val atmosphere = atmosphereProvider()
    val adaptiveAccent = atmosphere.getAdaptiveAccent()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryCard.copy(alpha = 0.6f + (0.4f * atmosphere.surfaceRichness))
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Text(
                text = "Behavioral Insights",
                style = MaterialTheme.typography.labelSmall,
                color = adaptiveAccent.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Pace Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightItem(
                    label = "Daily Pace",
                    value = "₹${insights.averageDailySavings.toInt()}",
                    accentColor = adaptiveAccent,
                    modifier = Modifier.weight(1f)
                )
                InsightItem(
                    label = "Weekly Pace",
                    value = "₹${insights.averageWeeklySavings.toInt()}",
                    accentColor = adaptiveAccent,
                    modifier = Modifier.weight(1f)
                )
                InsightItem(
                    label = "Longest Streak",
                    value = "${insights.longestStreak} Days",
                    accentColor = adaptiveAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.1f * atmosphere.surfaceRichness))
            Spacer(modifier = Modifier.height(20.dp))

            // 2. Forecast Section
            Text(
                text = "Forecast",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText.copy(alpha = 0.4f + (0.1f * atmosphere.surfaceRichness)),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val forecastText = if (insights.estimatedDaysRemaining != null) {
                if (insights.estimatedDaysRemaining == 0) "Journey complete."
                else "Approximately ${insights.estimatedDaysRemaining} days remain at current pace."
            } else {
                "More data needed for projection."
            }

            Text(
                text = forecastText,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.8f + (0.2f * atmosphere.surfaceRichness)),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Behavioral Reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElevatedCard.copy(alpha = 0.2f + (0.2f * atmosphere.surfaceRichness)))
                    .padding(16.dp)
            ) {
                Text(
                    text = insights.reflectionInsight,
                    style = MaterialTheme.typography.labelMedium,
                    color = adaptiveAccent.copy(alpha = 0.8f),
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun InsightItem(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = accentColor.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
    }
}
