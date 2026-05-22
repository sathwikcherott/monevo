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
    val consistency = viewModel.consistencyStats
    val isFreshStart = viewModel.totalSaved == 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
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
                Spacer(modifier = Modifier.height(16.dp))
                
                CircularProgressSection(
                    progress = viewModel.progress,
                    totalSaved = viewModel.totalSaved
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
                        modifier = Modifier.weight(1.1f)
                    )
                    AnalyticsCard(
                        title = "Completed",
                        value = "${viewModel.tiles.count { it.isCompleted }} tiles",
                        isHighlighted = true,
                        modifier = Modifier.weight(0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                TrendChart(heights = viewModel.weeklyMomentum)

                Spacer(modifier = Modifier.height(20.dp))

                MilestonesProgress(totalSaved = viewModel.totalSaved)

                Spacer(modifier = Modifier.height(20.dp))

                ConsistencySection(
                    streakDays = consistency.streak,
                    bestWeekAmount = consistency.bestWeek,
                    avgDailyAmount = consistency.avgDaily
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
