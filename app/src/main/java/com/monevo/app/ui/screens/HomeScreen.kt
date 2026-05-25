package com.monevo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.CelebrationType
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.components.*
import com.monevo.app.ui.theme.PrimaryText
import com.monevo.app.debug.DebugHapticController
import com.monevo.app.ui.motion.LocalMotionSettings
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(viewModel: SavingsViewModel) {
    val motionSettings = LocalMotionSettings.current
    val groupedTiles = viewModel.groupedTiles
    var expandedSectionIndex by remember { mutableIntStateOf(0) }
    var showConfetti by remember { mutableStateOf(false) }
    var showRecognitionGlow by remember { mutableStateOf(false) }
    var pulsingMilestoneId by remember { mutableStateOf<Int?>(null) }
    var celebrationTrigger by remember { mutableStateOf<CelebrationType?>(null) }

    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    // Synchronize trigger state with ViewModel
    LaunchedEffect(viewModel.activeCelebration) {
        if (viewModel.activeCelebration != null) {
            celebrationTrigger = viewModel.activeCelebration
        }
    }

    // Trigger sequenced celebration for final goal
    // Uses a separate state to ensure the sequence finishes even if the dialog is dismissed early
    LaunchedEffect(celebrationTrigger) {
        val celebration = celebrationTrigger ?: return@LaunchedEffect
        
        if (celebration is CelebrationType.FinalGoal) {
            // 1. Completion Pause / Recognition Moment
            delay(motionSettings.scaleDuration(400, 1.5f).toLong())
            
            // 2. Premium Haptic Feedback
            if (viewModel.isHapticsEnabled) {
                val duration = motionSettings.scaleDuration(200, 0.6f).toLong()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(duration)
                }
                DebugHapticController.onHapticExecuted(true)
            }
            
            // 3. Progress Completion Pulse
            showRecognitionGlow = true
            delay(motionSettings.scaleDuration(800).toLong())
            showRecognitionGlow = false
            
            // 4. Delayed Confetti Trigger (Always release)
            showConfetti = true
        } else if (celebration is CelebrationType.MilestoneReached) {
            val groupId = viewModel.groupedTiles.find { it.rangeEnd == celebration.amount }?.id
            pulsingMilestoneId = groupId

            if (viewModel.isHapticsEnabled) {
                delay(motionSettings.scaleValue(200f, 100f).toLong())
                val duration = motionSettings.scaleDuration(100, 0.6f).toLong()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(duration)
                }
                DebugHapticController.onHapticExecuted(true)
            }
            
            delay(motionSettings.scaleDuration(1200).toLong())
            pulsingMilestoneId = null
        }
        
        // Reset local trigger
        celebrationTrigger = null
    }

    // Celebration Dialog
    viewModel.activeCelebration?.let { celebration ->
        CelebrationDialog(
            celebration = celebration,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Monevo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            TotalSavedCard(
                totalProvider = { viewModel.totalSaved },
                progressProvider = { viewModel.progress },
                completedCountProvider = { viewModel.tiles.count { it.isCompleted } },
                totalCountProvider = { viewModel.tiles.size },
                goalProvider = { viewModel.goalAmount },
                isGlowActive = showRecognitionGlow
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedTiles.forEachIndexed { index, group ->
                    val isExpanded = expandedSectionIndex == index && !group.isLocked

                    item(key = "header_${group.id}") {
                        MilestoneAccordionHeader(
                            name = group.name,
                            isExpanded = isExpanded,
                            isLocked = group.isLocked,
                            isGlowActive = pulsingMilestoneId == group.id,
                            onClick = {
                                if (!group.isLocked) {
                                    expandedSectionIndex = if (isExpanded) -1 else index
                                }
                            }
                        )
                    }

                    item(key = "content_${group.id}") {
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                                    expandVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)),
                            exit = fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                                    shrinkVertically(animationSpec = tween(400, easing = FastOutSlowInEasing))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 16.dp)
                            ) {
                                TileGrid(
                                    tiles = group.tiles,
                                    onTileClick = { viewModel.toggleTile(it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showConfetti) {
            PremiumConfettiOverlay(
                onAnimationEnd = { showConfetti = false }
            )
        }
    }
}
