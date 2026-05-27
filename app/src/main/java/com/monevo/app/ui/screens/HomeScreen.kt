package com.monevo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.CelebrationType
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.components.*
import com.monevo.app.ui.theme.*
import com.monevo.app.debug.DebugHapticController
import com.monevo.app.ui.motion.LocalMotionSettings
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(viewModel: SavingsViewModel) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    
    val groupedTiles = viewModel.groupedTiles
    var expandedSectionIndex by remember { mutableIntStateOf(0) }
    var showConfetti by remember { mutableStateOf(false) }
    var showRecognitionGlow by remember { mutableStateOf(false) }
    var pulsingMilestoneId by remember { mutableStateOf<Int?>(null) }
    var celebrationTrigger by remember { mutableStateOf<CelebrationType?>(null) }
    var showFreshStartMessage by remember { mutableStateOf(false) }
    
    var isEntering by remember { 
        mutableStateOf(viewModel.isFreshStartArrival || viewModel.isAppLaunchEntrance) 
    }
    
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    LaunchedEffect(viewModel.isFreshStartArrival, viewModel.isAppLaunchEntrance) {
        if (viewModel.isFreshStartArrival || viewModel.isAppLaunchEntrance) {
            isEntering = true
            
            if (viewModel.isFreshStartArrival) {
                showFreshStartMessage = true
                delay(3000)
                showFreshStartMessage = false
                viewModel.isFreshStartArrival = false
            }
            
            if (viewModel.isAppLaunchEntrance) {
                delay(if (isReducedMotion) 500 else 2000)
                viewModel.isAppLaunchEntrance = false
            }
            
            isEntering = false
        }
    }

    LaunchedEffect(viewModel.activeCelebration) {
        if (viewModel.activeCelebration != null) {
            celebrationTrigger = viewModel.activeCelebration
        }
    }

    LaunchedEffect(celebrationTrigger) {
        val celebration = celebrationTrigger ?: return@LaunchedEffect
        
        if (celebration is CelebrationType.FinalGoal) {
            delay(motionSettings.scaleDuration(400, 1.5f).toLong())
            
            if (viewModel.isHapticsEnabled) {
                val duration = motionSettings.scaleDuration(200, 0.6f).toLong()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(duration)
                }
                DebugHapticController.onHapticExecuted(true)
            }
            
            showRecognitionGlow = true
            delay(motionSettings.scaleDuration(800).toLong())
            showRecognitionGlow = false
            
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
        
        celebrationTrigger = null
    }

    viewModel.activeCelebration?.let { celebration ->
        CelebrationDialog(
            celebration = celebration,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (viewModel.isReconfiguring) 0f else 1f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(600), 
            easing = FastOutSlowInEasing
        ),
        label = "contentAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .graphicsLayer { alpha = contentAlpha }
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monevo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )

                AnimatedVisibility(
                    visible = showFreshStartMessage,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = "Fresh start activated",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftAccentPink,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CinematicEntrance(index = 0, isTriggered = isEntering) {
                TotalSavedCard(
                    totalProvider = { viewModel.totalSaved },
                    progressProvider = { viewModel.progress },
                    completedCountProvider = { viewModel.tiles.count { it.isCompleted } },
                    totalCountProvider = { viewModel.tiles.size },
                    goalProvider = { viewModel.goalAmount },
                    isGlowActive = showRecognitionGlow,
                    atmosphere = viewModel.atmosphere
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedTiles.forEachIndexed { index, group ->
                    val isExpanded = expandedSectionIndex == index && !group.isLocked

                    item(key = "header_${group.id}") {
                        CinematicEntrance(index = index + 1, isTriggered = isEntering) {
                            MilestoneAccordionHeader(
                                name = group.name,
                                isExpanded = isExpanded,
                                isLocked = group.isLocked,
                                isGlowActive = pulsingMilestoneId == group.id,
                                atmosphere = viewModel.atmosphere,
                                onClick = {
                                    if (!group.isLocked) {
                                        expandedSectionIndex = if (isExpanded) -1 else index
                                    }
                                }
                            )
                        }
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
                                    onTileClick = { viewModel.toggleTile(it) },
                                    isEntering = isEntering,
                                    baseStaggerIndex = (index + 2) * 5
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

@Composable
fun CinematicEntrance(
    index: Int,
    isTriggered: Boolean,
    content: @Composable () -> Unit
) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    
    var hasTriggered by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(!isTriggered) }

    LaunchedEffect(isTriggered) {
        if (isTriggered && !hasTriggered) {
            hasTriggered = true
            isVisible = false
            if (!isReducedMotion) {
                delay(100L + (index * 80L))
            }
            isVisible = true
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(if (isReducedMotion) 300 else 550, easing = EaseOutCubic),
        label = "entranceAlpha"
    )

    val translateY by animateDpAsState(
        targetValue = if (isVisible || isReducedMotion) 0.dp else 12.dp,
        animationSpec = tween(550, easing = EaseOutCubic),
        label = "entranceTranslateY"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = translateY.toPx()
        }
    ) {
        content()
    }
}
