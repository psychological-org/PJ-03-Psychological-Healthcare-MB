package com.example.beaceful.core.network.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

object AuthDataStore {
    private val TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    suspend fun saveTokens(context: Context, accessToken: String, refreshToken: String?) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = accessToken
            if (refreshToken != null) {
                prefs[REFRESH_TOKEN_KEY] = refreshToken
            }
            Log.d("AuthDataStore", "Saved tokens: accessToken=$accessToken, refreshToken=$refreshToken")
        }
    }

    suspend fun getToken(context: Context): String? {
        return context.dataStore.data.map { prefs -> prefs[TOKEN_KEY] }.first()
    }

    suspend fun getRefreshToken(context: Context): String? {
        return context.dataStore.data.map { prefs -> prefs[REFRESH_TOKEN_KEY] }.first()
    }

    suspend fun clearTokens(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }
}