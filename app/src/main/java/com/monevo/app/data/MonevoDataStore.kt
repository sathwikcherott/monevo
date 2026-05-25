package com.monevo.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "monevo_prefs")

class MonevoDataStore(private val context: Context) {

    companion object {
        // Format: "id:timestamp"
        private val COMPLETED_TILES_DATA_KEY = stringSetPreferencesKey("completed_tiles_data")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val SHOWN_CELEBRATIONS_KEY = stringSetPreferencesKey("shown_celebrations")
        private val GOAL_AMOUNT_KEY = intPreferencesKey("goal_amount")
    }

    val completedTilesData: Flow<Map<Int, Long>> = context.dataStore.data.map { preferences ->
        preferences[COMPLETED_TILES_DATA_KEY]?.mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val id = parts[0].toIntOrNull()
                val timestamp = parts[1].toLongOrNull()
                if (id != null && timestamp != null) id to timestamp else null
            } else null
        }?.toMap() ?: emptyMap()
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    val shownCelebrationIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SHOWN_CELEBRATIONS_KEY] ?: emptySet()
    }

    val goalAmount: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[GOAL_AMOUNT_KEY]
    }

    suspend fun saveProgress(completedData: Map<Int, Long>) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TILES_DATA_KEY] = completedData.map { "${it.key}:${it.value}" }.toSet()
        }
    }

    suspend fun markCelebrationShown(milestoneId: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[SHOWN_CELEBRATIONS_KEY] ?: emptySet()
            preferences[SHOWN_CELEBRATIONS_KEY] = current + milestoneId
        }
    }

    suspend fun saveOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    suspend fun saveGoalAmount(amount: Int) {
        context.dataStore.edit { preferences ->
            preferences[GOAL_AMOUNT_KEY] = amount
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.remove(COMPLETED_TILES_DATA_KEY)
            preferences.remove(ONBOARDING_COMPLETED_KEY)
            preferences.remove(SHOWN_CELEBRATIONS_KEY)
            preferences.remove(GOAL_AMOUNT_KEY)
        }
    }
}
