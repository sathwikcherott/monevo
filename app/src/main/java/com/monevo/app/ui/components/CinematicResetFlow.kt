package com.monevo.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Stages for the cinematic reset experience.
 */
enum class ResetStage {
    CONFIRMATION,
    TRANSITION,
    ANIMATION,
    COMPLETION
}

@Composable
fun CinematicResetFlow(
    isVisible: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onAnimationComplete: () -> Unit,
) {
    var currentStage by remember { mutableStateOf(ResetStage.CONFIRMATION) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(isVisible) {
        if (isVisible) {
            currentStage = ResetStage.CONFIRMATION
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = (isVisible && currentStage == ResetStage.CONFIRMATION),
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            MonevoResetConfirmation(
                onConfirm = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    currentStage = ResetStage.TRANSITION
                },
                onCancel = onCancel
            )
        }

        if (isVisible && currentStage != ResetStage.CONFIRMATION) {
            CinematicResetAnimation {
                if (currentStage == ResetStage.TRANSITION) {
                    currentStage = ResetStage.ANIMATION
                } else if (currentStage == ResetStage.ANIMATION) {
                    currentStage = ResetStage.COMPLETION
                    onConfirm()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAnimationComplete()
                }
            }
        }
    }
}

@Composable
fun MonevoResetConfirmation(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            color = SurfaceBase,
            shape = RoundedCornerShape(32.dp),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start Fresh?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your progress, streaks, and milestones will reset.\nYour journey can begin again anytime.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRose.copy(alpha = 0.12f),
                        contentColor = ErrorRose
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Begin Fresh",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CinematicResetAnimation(onFinishStage: () -> Unit) {
    var animationTriggered by remember { mutableStateOf(false) }
    var currentTextIndex by remember { mutableIntStateOf(0) }
    
    val messages = listOf(
        "Closing your previous chapter...",
        "Clearing distractions...",
        "Preparing a fresh start...",
        "Momentum begins again."
    )
    
    // Duration derived from text sequence pacing: 
    // 600 (init) + 4 * 1800 (text) + 800 (final) = 8600ms total
    val totalDuration = 8600

    val transition = updateTransition(targetState = animationTriggered, label = "resetPhases")

    val bgAlpha by transition.animateFloat(
        transitionSpec = { tween(1500, easing = EaseInOutSine) },
        label = "bgAlpha"
    ) { if (it) 1f else 0f }

    // Phase-specific ring evolution - Heavily weighted towards the end to prevent early "completion" feel
    val ringDissolve by transition.animateFloat(
        transitionSpec = { 
            keyframes {
                durationMillis = totalDuration
                1f at 0
                0f at 1200 with FastOutSlowInEasing
                0f at totalDuration
            }
        },
        label = "ringDissolve"
    ) { if (it) 0f else 1f }

    val ringRebuild by transition.animateFloat(
        transitionSpec = { 
            keyframes {
                durationMillis = totalDuration
                0f at 0
                0.02f at 1200 // Almost nothing during first phrase
                0.12f at 2400 // Minimal progress during second phrase
                0.35f at 4500 // Still visibly incomplete at mid-sequence
                0.65f at 6500 // Open gap during third phrase
                0.90f at 8000 // Final stabilization stretch
                1f at 8400 with FastOutSlowInEasing
                1f at totalDuration
            }
        },
        label = "ringRebuild"
    ) { if (it) 1f else 0f }

    // Subtle atmospheric motion that never stops
    val ringRotation by transition.animateFloat(
        transitionSpec = { tween(totalDuration, easing = LinearEasing) },
        label = "ringRotation"
    ) { if (it) 30f else 0f }

    // Glow that evolves with the narrative stages
    val evolvingGlowAlpha by transition.animateFloat(
        transitionSpec = {
            keyframes {
                durationMillis = totalDuration
                0f at 0
                0.02f at 2000
                0.05f at 5000
                0.08f at 8000 with EaseOutSine
                0.04f at 8600 // Settles to a calm resting state
            }
        },
        label = "evolvingGlow"
    ) { if (it) 0f else 0f }

    val fullTimelineProgress by transition.animateFloat(
        transitionSpec = { tween(totalDuration, easing = LinearEasing) },
        label = "fullProgress"
    ) { if (it) 1f else 0f }

    val textAlpha by transition.animateFloat(
        transitionSpec = { tween(1200, easing = EaseInOutSine) },
        label = "textAlpha"
    ) { if (it) 1f else 0f }

    LaunchedEffect(Unit) {
        animationTriggered = true
        onFinishStage() 
        
        delay(600)
        for (i in messages.indices) {
            currentTextIndex = i
            delay(1800)
        }
        
        delay(800)
        onFinishStage() 
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = bgAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Layer 1: Ambient Context
            repeat(12) { i ->
                AmbientParticle(
                    index = i, 
                    delayMillis = i * 250,
                    animationTriggered = animationTriggered
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Layer Container
                Box(
                    modifier = Modifier.size(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Layer 2: Rotating Ring & Glow (Transforming Energy)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(ringRotation),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .blur(40.dp)
                                .alpha(evolvingGlowAlpha * 0.5f)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(SoftAccentPink.copy(alpha = 0.3f), Color.Transparent)
                                    ),
                                    shape = CircleShape
                                )
                        )

                        Canvas(modifier = Modifier.size(200.dp)) {
                            drawArc(
                                color = DividerStroke.copy(alpha = 0.15f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )

                            if (ringDissolve > 0.01f) {
                                drawArc(
                                    color = MainProgressGreen.copy(alpha = 0.4f * ringDissolve),
                                    startAngle = -90f,
                                    sweepAngle = 360f * ringDissolve,
                                    useCenter = false,
                                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            if (ringRebuild > 0.01f) {
                                // Subtly evolving opacity for reconstruction feel
                                val reconstructionAlpha = 0.3f + (0.2f * ringRebuild)
                                drawArc(
                                    color = SoftAccentPink.copy(alpha = reconstructionAlpha),
                                    startAngle = -90f,
                                    sweepAngle = 360f * ringRebuild,
                                    useCenter = false,
                                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }

                    // Layer 3: Static Center Text (Stable Core - No Ring Transforms)
                    val percentageText = remember(ringRebuild) {
                        if (ringRebuild >= 0.99f) "Fresh Start"
                        else "${(ringRebuild * 100).toInt()}%"
                    }
                    
                    val textFadeAlpha by animateFloatAsState(
                        targetValue = if (ringRebuild > 0.05f) 1f else 0f,
                        animationSpec = tween(600, delayMillis = 400),
                        label = "centerTextFade"
                    )

                    Text(
                        text = percentageText,
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary.copy(alpha = (ringRebuild * 0.9f).coerceIn(0f, 0.9f)),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(textFadeAlpha)
                    )
                    
                    // Central Background Pulse (Static)
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .alpha(evolvingGlowAlpha * 0.8f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(SoftAccentPink.copy(alpha = 0.2f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))

                // Cinematic Typography
                AnimatedContent(
                    targetState = currentTextIndex,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(1200, easing = EaseInOutSine)) + 
                         slideInVertically(animationSpec = tween(1200, easing = EaseOutCubic)) { it / 4 }).togetherWith(
                         fadeOut(animationSpec = tween(1000, easing = EaseInCubic)) + 
                         slideOutVertically(animationSpec = tween(1000)) { -it / 4 })
                    },
                    label = "cinematicText"
                ) { index ->
                    Text(
                        text = messages[index],
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary.copy(alpha = textAlpha),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))

                // Ambient Loading Line
                AmbientLoadingLine(
                    isVisible = animationTriggered,
                    progress = fullTimelineProgress
                )
            }
        }
    }
}

@Composable
fun AmbientLoadingLine(isVisible: Boolean, progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "lineFlow")
    
    val sweepOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .width(180.dp)
            .height(1.dp)
            .alpha(if (isVisible) 0.6f else 0f),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF2A2228).copy(alpha = 0.5f))
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0.01f, 1f))
                .height(1.dp)
                .blur(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        0f to MainProgressGreen.copy(alpha = 0.2f),
                        sweepOffset to MainProgressGreen,
                        1f to MainProgressGreen.copy(alpha = 0.2f),
                        startX = 0f,
                        endX = 1000f
                    )
                )
                .shadow(
                    elevation = 4.dp,
                    spotColor = MainProgressGreen.copy(alpha = glowAlpha),
                    ambientColor = Color.Transparent
                )
        )
    }
}

@Composable
fun AmbientParticle(index: Int, delayMillis: Int, animationTriggered: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "drift")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, delayMillis = delayMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pAlpha"
    )
    
    val driftY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, delayMillis = delayMillis, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pDrift"
    )

    val xPos = ((index % 4) * 80).dp + (index * 5).dp
    val yPos = ((index / 4) * 150).dp

    Box(
        modifier = Modifier
            .offset(x = xPos - 120.dp, y = yPos - 200.dp + driftY.dp)
            .size(1.5.dp)
            .alpha(if (animationTriggered) alpha else 0f)
            .background(SoftAccentPink.copy(alpha = 0.4f), CircleShape)
    )
}
