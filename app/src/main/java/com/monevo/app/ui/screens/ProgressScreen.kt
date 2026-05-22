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
import com.monevo.app.ui.components.AnalyticsCard
import com.monevo.app.ui.components.CircularProgressSection
import com.monevo.app.ui.components.TrendChart
import com.monevo.app.ui.theme.PrimaryText

@Composable
fun ProgressScreen(viewModel: SavingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        CircularProgressSection(
            progress = viewModel.progress,
            totalSaved = viewModel.totalSaved
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsCard(
                title = "Completed",
                value = "${viewModel.tiles.count { it.isCompleted }}",
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                title = "Remaining",
                value = "₹%,d".format(viewModel.goalAmount - viewModel.totalSaved),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        TrendChart()
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
