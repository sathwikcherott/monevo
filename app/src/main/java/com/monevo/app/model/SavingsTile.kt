package com.monevo.app.model

import androidx.compose.runtime.Immutable

@Immutable
data class SavingsTile(
    val id: Int,
    val amount: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)
