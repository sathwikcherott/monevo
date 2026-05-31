package com.monevo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.debug.DebugHapticController
import com.monevo.app.ui.motion.LocalMotionSettings
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(viewModel: SavingsViewModel) {
    val motionSettings = LocalMotionSettings.current
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    // Stabilize state access to minimize parent recompositions
    val groupedTiles by remember { derivedStateOf { viewModel.groupedTiles } }
    var expandedSectionIndex by remember { mutableIntStateOf(0) }
    var showConfetti by remember { mutableStateOf(false) }
    var showRecognitionGlow by remember { mutableStateOf(false) }
    var pulsingMilestoneId by remember { mutableStateOf<Int?>(null) }
    var celebrationTrigger by remember { mutableStateOf<CelebrationType?>(null) }
    var showFreshStartMessage by remember { mutableStateOf(false) }
    
    // Architecture Refactor: Persistent entrance state to prevent re-triggering
    val isEntranceActive = remember { 
        viewModel.isAppLaunchEntrance || viewModel.isFreshStartArrival 
    }
    
    // Deterministic entrance orchestration
    var showTiles by remember { mutableStateOf(!isEntranceActive) }
    
    // Stabilized derived states for UI components
    val totalSaved = remember { derivedStateOf { viewModel.totalSaved } }
    val progress = remember { derivedStateOf { viewModel.progress } }
    val tilesCount = remember { derivedStateOf { viewModel.completedTilesCount } }
    val tilesTotal = remember { derivedStateOf { viewModel.totalTilesCount } }
    val goalAmount = remember { derivedStateOf { viewModel.goalAmount } }
    val atmosphere = remember { derivedStateOf { viewModel.atmosphere } }
    val journeyState = remember { derivedStateOf { viewModel.journeyState } }

    LaunchedEffect(isEntranceActive) {
        if (isEntranceActive) {
            delay(100)
            showTiles = true
            
            if (viewModel.isFreshStartArrival) {
                showFreshStartMessage = true
                delay(3000)
                showFreshStartMessage = false
                viewModel.isFreshStartArrival = false
            }
            if (viewModel.isAppLaunchEntrance) {
                viewModel.isAppLaunchEntrance = false
            }
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
            reflection = viewModel.currentReflection,
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
        val columnModifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .let { 
                if (contentAlpha < 1f) {
                    it.graphicsLayer { alpha = contentAlpha }
                } else it
            }

        Column(modifier = columnModifier) {
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

            // Architecture: Unified entrance for top card
            CinematicEntrance(index = 0, isTriggered = showTiles) {
                TotalSavedCard(
                    totalProvider = { totalSaved.value },
                    progressProvider = { progress.value },
                    completedCountProvider = { tilesCount.value },
                    totalCountProvider = { tilesTotal.value },
                    goalProvider = { goalAmount.value },
                    atmosphereProvider = { atmosphere.value },
                    journeyStateProvider = { journeyState.value },
                    isGlowActive = showRecognitionGlow
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Architecture Refactor: LazyVerticalGrid for stable, high-performance tile rendering
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groupedTiles.forEachIndexed { groupIndex, group ->
                    val isExpanded = expandedSectionIndex == groupIndex && !group.isLocked

                    // Stable Header Item
                    item(
                        key = "header_${group.id}", 
                        span = { GridItemSpan(5) }
                    ) {
                        CinematicEntrance(index = groupIndex + 1, isTriggered = showTiles) {
                            MilestoneAccordionHeader(
                                name = group.name,
                                isExpanded = isExpanded,
                                isLocked = group.isLocked,
                                isGlowActive = pulsingMilestoneId == group.id,
                                atmosphere = atmosphere.value,
                                onClick = {
                                    if (!group.isLocked) {
                                        expandedSectionIndex = if (isExpanded) -1 else groupIndex
                                    }
                                }
                            )
                        }
                    }

                    // Architecture Refactor: Individual tile items for maximum stability and scroll performance
                    // Items are only present in composition when expanded, avoiding unnecessary mounting.
                    if (isExpanded) {
                        items(
                            items = group.tiles,
                            key = { it.id } // STABLE KEY: Required for 120Hz performance
                        ) { tile ->
                            // Use lightweight stagger wrapper
                            TileEntranceWrapper(
                                index = group.tiles.indexOf(tile), // Local stagger for calmness
                                isTriggered = showTiles
                            ) {
                                SavingsTileItem(
                                    tile = tile, 
                                    onClick = { viewModel.toggleTile(tile.id) }
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
fun TileEntranceWrapper(
    index: Int,
    isTriggered: Boolean,
    content: @Composable () -> Unit
) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled

    // Architecture: Lightweight deterministic stagger without coroutines/delays
    val alpha by animateFloatAsState(
        targetValue = if (isTriggered) 1f else 0f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(400, 0.5f),
            delayMillis = (index * (if (isReducedMotion) 10 else 25)).coerceAtMost(if (isReducedMotion) 200 else 500), // Calm, capped stagger
            easing = EaseOutCubic
        ),
        label = "tileAlpha"
    )

    val translateY by animateFloatAsState(
        targetValue = if (isTriggered || isReducedMotion) 0f else 12f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(500, 0.5f),
            delayMillis = (index * 25).coerceAtMost(500),
            easing = EaseOutCubic
        ),
        label = "tileTranslation"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            if (!isReducedMotion) {
                this.translationY = translateY.dp.toPx()
            }
        }
    ) {
        content()
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
    
    // Architecture: Deterministic progress animation without internal state or LaunchedEffects
    val entranceProgress by animateFloatAsState(
        targetValue = if (isTriggered) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isReducedMotion) 200 else 500, 
            delayMillis = index * (if (isReducedMotion) 20 else 60), // Sequential staging
            easing = EaseOutCubic
        ),
        label = "entranceProgress"
    )

    // Optimization: Avoid graphicsLayer once entrance is complete to reduce compositing overhead
    val modifier = if (entranceProgress < 1f) {
        Modifier.graphicsLayer {
            alpha = entranceProgress
            if (!isReducedMotion) {
                translationY = (1f - entranceProgress) * 12.dp.toPx()
            }
        }
    } else Modifier

    Box(modifier = modifier) {
        content()
    }
}
