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
import com.monevo.app.ui.components.InsightItem
import com.monevo.app.ui.theme.PrimaryText

@Composable
fun InsightsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Insights",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        InsightItem(
            title = "Savings Velocity",
            description = "You are saving 20% faster than last month. Keep it up!",
            emoji = "🚀"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InsightItem(
            title = "Consistency Streak",
            description = "You've saved for 5 consecutive days. You're on fire!",
            emoji = "🔥"
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        InsightItem(
            title = "Smart Suggestion",
            description = "Tapping just four more ₹150 tiles will reach your weekly goal.",
            emoji = "💡"
        )
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}
