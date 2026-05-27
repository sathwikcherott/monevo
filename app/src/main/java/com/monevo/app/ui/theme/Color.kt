package com.monevo.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Monevo Premium AMOLED Palette
 * "Quiet progress softly illuminated in darkness"
 */

// Pure AMOLED Black (use sparingly for void/edges)
val AmoledBlack = Color(0xFF000000)

// Background Layers
val PrimaryBackground = Color(0xFF0B090B)
val SurfaceBase = Color(0xFF151116)
val SurfaceElevated = Color(0xFF1D171D)
val SurfaceModal = Color(0xFF251E25)
val DividerStroke = Color(0xFF302730)

// Accent System (Desaturated for AMOLED comfort)
val PrimaryAccentPink = Color(0xFFD6A0B7)
val SoftAccentPink = Color(0xFFE4C4D2)

// Progress / Savings Green
val MainProgressGreen = Color(0xFF74C79B)
val SoftProgressGreen = Color(0xFFA4DFC0)
val DeepProgressGreen = Color(0xFF4C9670)

// Typography (Soften whites to reduce OLED eye fatigue)
val TextPrimary = Color(0xFFF1EEF0)
val TextSecondary = Color(0xFFB3AAB0)
val TextMuted = Color(0xFF756D73)

// Feedback Colors
val WarningAmber = Color(0xFFC8A061)
val ErrorRose = Color(0xFFBC6C74)

// Legacy Aliases (mapped to new AMOLED palette)
val Background = PrimaryBackground
val PrimaryCard = SurfaceBase
val ElevatedCard = SurfaceElevated
val AccentGold = PrimaryAccentPink
val SoftGold = SoftAccentPink
val SuccessGreen = MainProgressGreen
val PrimaryText = TextPrimary
val SecondaryText = TextSecondary
val DividerColor = DividerStroke
