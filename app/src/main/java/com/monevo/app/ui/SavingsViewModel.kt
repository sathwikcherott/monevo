package com.monevo.app.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.monevo.app.model.SavingsTile

class SavingsViewModel : ViewModel() {
    val tiles = mutableStateListOf<SavingsTile>().apply {
        addAll(listOf(50, 100, 150, 200, 300, 500, 50, 100, 150, 200, 300, 500, 50, 100, 150, 200, 300, 500, 50, 100, 150, 200).mapIndexed { index, amount ->
            SavingsTile(index, amount)
        })
    }

    val totalSaved: Int
        get() = tiles.filter { it.isCompleted }.sumOf { it.amount }

    val goalAmount: Int = 10000

    val progress: Float
        get() = totalSaved.toFloat() / goalAmount

    fun toggleTile(id: Int) {
        val index = tiles.indexOfFirst { it.id == id }
        if (index != -1) {
            tiles[index] = tiles[index].copy(isCompleted = !tiles[index].isCompleted)
        }
    }
}
