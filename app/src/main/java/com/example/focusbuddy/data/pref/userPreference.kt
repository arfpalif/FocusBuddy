package com.example.focusbuddy.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferenceKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

class UserPreference(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")
    private val dataStore = context.dataStore

    suspend fun saveLoginState(isLoggedIn: Boolean) {
        dataStore.edit { prefs ->
            prefs[UserPreferenceKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[UserPreferenceKeys.IS_LOGGED_IN] ?: false }
}
