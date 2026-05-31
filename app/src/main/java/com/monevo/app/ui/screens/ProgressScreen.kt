package com.monevo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.components.*
import com.monevo.app.ui.theme.PrimaryText
import com.monevo.app.ui.theme.TextSecondary

@Composable
fun ProgressScreen(
    viewModel: SavingsViewModel,
    onNavigateHome: () -> Unit
) {
    val totalSaved = viewModel.totalSaved
    val isFreshStart = totalSaved == 0
    
    // Defer reading specific stats to avoid top-level recomposition
    val progressProvider = { viewModel.progress }
    val atmosphereProvider = { viewModel.atmosphere }
    val totalSavedProvider = { viewModel.totalSaved }
    val journeyStateProvider = { viewModel.journeyState }
    val reflectionProvider = { viewModel.currentReflection }
    val insightDataProvider = { viewModel.insightData }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))
        
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        if (isFreshStart) {
            FreshStartView(onBeginSaving = onNavigateHome)
        } else {
            val journeyState = journeyStateProvider()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Adaptive banner for different journey stages
                if (progressProvider() < 1f) {
                    MomentumBanner(
                        title = journeyState.title,
                        message = journeyState.message
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                CircularProgressSection(
                    progressProvider = progressProvider,
                    totalSavedProvider = totalSavedProvider,
                    isMomentumActive = progressProvider() in 0.01f..0.25f,
                    atmosphereProvider = atmosphereProvider
                )
                
                Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
                    Text(
                        text = if (progressProvider() >= 1f) "Mission Accomplished" else "Current Stage",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = journeyState.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = journeyState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AnalyticsCard(
                        title = "Remaining",
                        value = "₹%,d".format(viewModel.goalAmount - viewModel.totalSaved),
                        isHighlighted = false,
                        atmosphere = viewModel.atmosphere,
                        modifier = Modifier.weight(1.1f)
                    )
                    AnalyticsCard(
                        title = "Completed",
                        value = "${viewModel.completedTilesCount} tiles",
                        isHighlighted = true,
                        atmosphere = viewModel.atmosphere,
                        modifier = Modifier.weight(0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                TrendChart(heightsProvider = { viewModel.weeklyMomentum })

                Spacer(modifier = Modifier.height(20.dp))

                InsightsCard(insightDataProvider = insightDataProvider)

                Spacer(modifier = Modifier.height(20.dp))

                MilestonesProgress(
                    totalSavedProvider = totalSavedProvider,
                    goalAmountProvider = { viewModel.goalAmount },
                    atmosphereProvider = atmosphereProvider
                )

                val reflection = reflectionProvider()
                Column(
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 8.dp, start = 4.dp, end = 4.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Reflections",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = reflection.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reflection.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary.copy(alpha = 0.7f),
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                ConsistencySection(
                    streakDaysProvider = { viewModel.consistencyStats.streak },
                    bestWeekAmountProvider = { viewModel.consistencyStats.bestWeek },
                    avgDailyAmountProvider = { viewModel.consistencyStats.avgDaily }
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
