package com.pause.frontend.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private const val DS_NAME = "session_prefs"
private val Context.dataStore by preferencesDataStore(DS_NAME)

object SessionKeys {
    val EMAIL = stringPreferencesKey("email")
    val USER_ID = longPreferencesKey("user_id")
    val PET_ID = longPreferencesKey("pet_id")
}

class SessionPrefs(private val context: Context) {
    val emailFlow = context.dataStore.data.map { it[SessionKeys.EMAIL] }
    val userIdFlow = context.dataStore.data.map { it[SessionKeys.USER_ID] }
    val petIdFlow  = context.dataStore.data.map { it[SessionKeys.PET_ID] }

    suspend fun save(email: String, userId: Long, petId: Long) {
        context.dataStore.edit {
            it[SessionKeys.EMAIL] = email
            it[SessionKeys.USER_ID] = userId
            it[SessionKeys.PET_ID] = petId
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}