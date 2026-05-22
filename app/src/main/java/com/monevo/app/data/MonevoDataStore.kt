package com.monevo.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "monevo_prefs")

class MonevoDataStore(private val context: Context) {

    companion object {
        // Format: "id:timestamp"
        private val COMPLETED_TILES_DATA_KEY = stringSetPreferencesKey("completed_tiles_data")
        private val UNLOCKED_MILESTONES_KEY = intPreferencesKey("unlocked_milestones")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    /**
     * Returns a map of Tile ID to Completion Timestamp
     */
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

    val unlockedMilestoneCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[UNLOCKED_MILESTONES_KEY] ?: 1
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun saveProgress(completedData: Map<Int, Long>, unlockedCount: Int) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TILES_DATA_KEY] = completedData.map { "${it.key}:${it.value}" }.toSet()
            preferences[UNLOCKED_MILESTONES_KEY] = unlockedCount
        }
    }

    suspend fun saveOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }
}
