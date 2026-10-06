package com.aly.travelio.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class AppDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val SEEDING_COMPLETED = booleanPreferencesKey("seedingCompleted")
        val IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
        val USER_ID = stringPreferencesKey("userId")
    }

    val seedingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SEEDING_COMPLETED] ?: false
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = isLoggedIn }
    }

    suspend fun setUserId(userId: String) {
        context.dataStore.edit { it[USER_ID] = userId }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }

    val userId: Flow<String> = context.dataStore.data.map { it[USER_ID] ?: "" }

    suspend fun setSeedingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SEEDING_COMPLETED] = completed
        }
    }


}
