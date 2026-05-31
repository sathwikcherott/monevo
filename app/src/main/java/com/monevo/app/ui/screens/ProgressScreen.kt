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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Momentum building is now percentage-based (2% to 30% of journey)
                if (progressProvider() in 0.02f..0.3f) {
                    MomentumBanner()
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                CircularProgressSection(
                    progressProvider = progressProvider,
                    totalSavedProvider = totalSavedProvider,
                    isMomentumActive = progressProvider() in 0.02f..0.3f,
                    atmosphereProvider = atmosphereProvider
                )
                
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

                MilestonesProgress(
                    totalSavedProvider = totalSavedProvider,
                    goalAmountProvider = { viewModel.goalAmount },
                    atmosphereProvider = atmosphereProvider
                )

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
