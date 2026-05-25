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
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveGold
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun ProfileScreen(viewModel: SavingsViewModel) {
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        MonevoResetDialog(
            onConfirm = {
                viewModel.resetProgress()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 1. Hero / Profile Section
        ProfileHeroCard(
            totalSaved = viewModel.totalSaved,
            goalAmount = viewModel.goalAmount,
            progress = viewModel.progress,
            atmosphere = viewModel.atmosphere
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 2. Savings Goal Section
        ProfileSection(title = "Savings Goal") {
            GoalPresetsRow(viewModel = viewModel)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 3. Experience Section
        ProfileSection(title = "Experience") {
            ProfileToggleOption(
                label = "Tactile Haptics",
                description = "Subtle touch feedback",
                initialValue = viewModel.isHapticsEnabled,
                onCheckedChange = { viewModel.updateHapticsEnabled(it) }
            )
            ProfileToggleOption(
                label = "Reduced Motion",
                description = "Softer UI transitions",
                initialValue = viewModel.isReducedMotionEnabled,
                onCheckedChange = { viewModel.updateReducedMotion(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 4. Data Section
        ProfileSection(title = "Data & Continuity") {
            ProfileActionOption(
                label = "Replay Onboarding",
                onClick = { viewModel.replayOnboarding() }
            )
            ProfileActionOption(
                label = "Reset Progress", 
                isDangerous = true,
                onClick = { showResetDialog = true }
            )
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
fun ProfileHeroCard(
    totalSaved: Int, 
    goalAmount: Int, 
    progress: Float,
    atmosphere: JourneyAtmosphere = JourneyAtmosphere.FreshStart
) {
    val isCompleted = progress >= 1f
    val motionSettings = LocalMotionSettings.current

    // Granular title state based on progress
    val title = when {
        progress >= 1f -> "Journey Completed"
        progress >= 0.95f -> "Final Stretch"
        progress >= 0.90f -> "Almost Complete"
        progress >= 0.80f -> "Approaching the Finish"
        progress >= 0.70f -> "Closing the Gap"
        progress >= 0.60f -> "Momentum Established"
        progress >= 0.50f -> "Strong Progress"
        progress >= 0.40f -> "Halfway There"
        progress >= 0.30f -> "Finding Consistency"
        progress >= 0.20f -> "Momentum Growing"
        progress >= 0.15f -> "Building Momentum"
        progress >= 0.10f -> "Steady Start"
        progress >= 0.05f -> "First Progress"
        progress > 0f -> "Getting Started"
        else -> "Ready to Begin"
    }

    // Atmospheric supporting message and visual evolution
    val subtitle = atmosphere.supportingText
    val adaptiveGold = atmosphere.getAdaptiveGold()
    
    val elevation = when {
        progress >= 1f -> 12.dp
        progress >= 0.95f -> 6.dp
        progress >= 0.70f -> 2.dp
        else -> 0.dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = motionSettings.scaleDp(elevation, if (elevation > 0.dp) 2.dp else 0.dp),
                shape = RoundedCornerShape(28.dp),
                spotColor = adaptiveGold.copy(alpha = if (progress >= 0.7f) 0.2f else 0f)
            ),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = adaptiveGold.copy(alpha = (0.7f + (progress * 0.3f)).coerceIn(0.7f, 1f)),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Stars,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "₹%,d".format(totalSaved),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText,
                        modifier = Modifier.padding(top = 4.dp)
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
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isCompleted) AccentGold else adaptiveGold,
                    trackColor = ElevatedCard.copy(alpha = 0.4f),
                )
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
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
fun GoalPresetsRow(viewModel: SavingsViewModel) {
    val currentGoal = viewModel.goalAmount
    val goals = listOf(10000, 25000, 50000, 100000)
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        goals.forEach { goal ->
            val label = "₹${goal / 1000}K"
            val isSelected = goal == currentGoal
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.updateGoal(goal) },
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
fun ProfileToggleOption(
    label: String, 
    description: String, 
    initialValue: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(!initialValue)
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
        
        MonevoToggle(checked = initialValue)
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
fun ProfileActionOption(label: String, isDangerous: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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

@Composable
fun MonevoResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Your Savings Journey?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "This will permanently clear your current savings progress, milestones, and completion history from this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    lineHeight = 22.sp
                )

                // Warning Box
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Important",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All progress tied to this journey will be removed locally. Make sure you truly want to begin again from the start.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }

                Text(
                    text = "This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Reset Progress",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = PrimaryCard,
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 8.dp
    )
}
