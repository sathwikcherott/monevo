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
        private val COMPLETED_TILES_KEY = stringSetPreferencesKey("completed_tiles")
        private val UNLOCKED_MILESTONES_KEY = intPreferencesKey("unlocked_milestones")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val completedTileIds: Flow<Set<Int>> = context.dataStore.data.map { preferences ->
        preferences[COMPLETED_TILES_KEY]?.map { it.toInt() }?.toSet() ?: emptySet()
    }

    val unlockedMilestoneCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[UNLOCKED_MILESTONES_KEY] ?: 2
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun saveProgress(completedIds: Set<Int>, unlockedCount: Int) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TILES_KEY] = completedIds.map { it.toString() }.toSet()
            preferences[UNLOCKED_MILESTONES_KEY] = unlockedCount
        }
    }

    suspend fun saveOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }
}
