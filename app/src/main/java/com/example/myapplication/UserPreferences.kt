package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_flight_prefs")

class UserPreferences(private val context: Context) {

    private val ORIGIN_HISTORY = stringSetPreferencesKey("origin_history")
    private val DEST_HISTORY = stringSetPreferencesKey("destination_history")

    val originHistory = context.dataStore.data.map { prefs ->
        prefs[ORIGIN_HISTORY] ?: emptySet()
    }

    val destHistory = context.dataStore.data.map { prefs ->
        prefs[DEST_HISTORY] ?: emptySet()
    }

    suspend fun saveOrigin(code: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[ORIGIN_HISTORY]?.toMutableSet() ?: mutableSetOf()
            set.add(code.uppercase())
            prefs[ORIGIN_HISTORY] = set
        }
    }

    suspend fun saveDestination(code: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[DEST_HISTORY]?.toMutableSet() ?: mutableSetOf()
            set.add(code.uppercase())
            prefs[DEST_HISTORY] = set
        }
    }
}
