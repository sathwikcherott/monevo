package com.monevo.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.theme.*

@Composable
fun SettingsScreen(viewModel: SavingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 1. Hero / Profile Section
        SettingsHeroCard(
            totalSaved = viewModel.totalSaved,
            goalAmount = viewModel.goalAmount,
            progress = viewModel.progress
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 2. Savings Goal Section
        SettingsSection(title = "Savings Goal") {
            GoalPresetsRow(currentGoal = viewModel.goalAmount)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 3. Experience Section
        SettingsSection(title = "Experience") {
            SettingsToggleOption(
                label = "Tactile Haptics",
                description = "Subtle touch feedback",
                initialValue = true
            )
            SettingsToggleOption(
                label = "Reduced Motion",
                description = "Softer UI transitions",
                initialValue = false
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 4. Data Section
        SettingsSection(title = "Data & Continuity") {
            SettingsActionOption(label = "Replay Onboarding")
            SettingsActionOption(label = "Reset Progress", isDangerous = true)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // 5. About Section
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Monevo v1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText.copy(alpha = 0.6f)
                )
                Text(
                    text = "Built for focused saving",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText.copy(alpha = 0.4f),
                    fontSize = 10.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SettingsHeroCard(totalSaved: Int, goalAmount: Int, progress: Float) {
    val remainingAmount = (goalAmount - totalSaved).coerceAtLeast(0)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Your Journey",
                style = MaterialTheme.typography.labelLarge,
                color = SoftGold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "₹%,d".format(totalSaved),
                        style = MaterialTheme.typography.headlineLarge, // Primary Focus
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "of ₹%,d goal".format(goalAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Supportive Percentage Badge
                Surface(
                    color = ElevatedCard,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Progress Connection
            Column {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = SoftGold.copy(alpha = 0.6f),
                    trackColor = ElevatedCard.copy(alpha = 0.4f),
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "₹%,d left".format(remainingAmount),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = SecondaryText,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Surface(
            color = PrimaryCard,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun GoalPresetsRow(currentGoal: Int) {
    val goals = listOf(10000, 25000, 50000, 0)
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        goals.forEach { goal ->
            val label = if (goal == 0) "Custom" else "₹${goal / 1000}K"
            val isSelected = goal == currentGoal || (goal == 0 && currentGoal !in listOf(10000, 25000, 50000))
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { /* Future Logic */ },
                color = if (isSelected) ElevatedCard else Background.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) SoftGold else SecondaryText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsToggleOption(label: String, description: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                checked = !checked 
            }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label, 
                style = MaterialTheme.typography.bodyMedium, 
                color = PrimaryText, 
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description, 
                style = MaterialTheme.typography.labelSmall, 
                color = SecondaryText.copy(alpha = 0.8f)
            )
        }
        
        MonevoToggle(checked = checked)
    }
}

@Composable
fun MonevoToggle(checked: Boolean) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) SoftGold else ElevatedCard,
        animationSpec = spring(),
        label = "trackColor"
    )
    
    val thumbColor by animateColorAsState(
        targetValue = if (checked) Background else SecondaryText.copy(alpha = 0.6f),
        animationSpec = spring(),
        label = "thumbColor"
    )
    
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 0.dp,
        animationSpec = spring(stiffness = 800f),
        label = "thumbOffset"
    )

    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 22.dp)
            .clip(CircleShape)
            .background(trackColor)
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(18.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Composable
fun SettingsActionOption(label: String, isDangerous: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Future Logic */ }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDangerous) MaterialTheme.colorScheme.error.copy(alpha = 0.8f) else PrimaryText,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryText.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
