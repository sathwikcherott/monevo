package com.monevo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import com.monevo.app.ui.motion.LocalMotionSettings
import kotlin.random.Random

/**
 * Premium celebration colors.
 */
private val ConfettiGold = Color(0xFFD4AF37)
private val ConfettiSoftGold = Color(0xFFF7E7CE)
private val ConfettiMutedPink = Color(0xFFE5B9B9)
private val ConfettiWhite = Color(0xFFFFFFFF)

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val speedY: Float,
    val speedX: Float,
    val rotationSpeed: Float,
    val delay: Int
)

@Composable
fun PremiumConfettiOverlay(
    durationMillis: Long = 4000L,
    onAnimationEnd: () -> Unit = {}
) {
    val motionSettings = LocalMotionSettings.current
    val effectiveDuration = motionSettings.scaleDuration(durationMillis.toInt()).toLong()
    val particleCount = if (motionSettings.isReducedMotionEnabled) 40 else 100
    
    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = -0.05f, // Start just above screen
                size = Random.nextFloat() * 10f + 6f,
                color = listOf(ConfettiGold, ConfettiSoftGold, ConfettiMutedPink, ConfettiWhite).random(),
                speedY = Random.nextFloat() * motionSettings.scaleValue(0.0003f, 0.00015f) + 0.0002f, 
                speedX = (Random.nextFloat() - 0.5f) * motionSettings.scaleValue(0.0001f, 0.00005f),
                rotationSpeed = Random.nextFloat() * motionSettings.scaleValue(2f, 0.5f) + 1f,
                delay = Random.nextInt(0, 1500)
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(effectiveDuration.toInt(), easing = LinearOutSlowInEasing)
        )
        onAnimationEnd()
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val t = progress.value

        particles.forEach { p ->
            // Calculate current time in animation for this particle
            val currentTime = t * effectiveDuration
            val particleElapsed = currentTime - p.delay

            if (particleElapsed <= 0) return@forEach

            // Normalize particle progress
            val pProgress = particleElapsed / (effectiveDuration - p.delay)
            if (pProgress >= 1f) return@forEach

            val currentY = (p.y + p.speedY * particleElapsed) * height
            val swing = if (motionSettings.isReducedMotionEnabled) 0f else 0.005f
            val currentX = (p.x + p.speedX * particleElapsed + Math.sin(particleElapsed.toDouble() * swing).toFloat() * 0.02f) * width
            val rotation = p.rotationSpeed * particleElapsed * 0.1f

            // Elegant fade out
            val alpha = if (pProgress > 0.7f) (1f - pProgress) / 0.3f else 1f

            withTransform({
                rotate(rotation, Offset(currentX + p.size / 2, currentY + p.size / 2))
            }) {
                drawRect(
                    color = p.color.copy(alpha = alpha),
                    topLeft = Offset(currentX, currentY),
                    size = Size(p.size, p.size * 0.7f)
                )
            }
        }
    }
}
