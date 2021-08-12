package com.duwna.biblo.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duwna.biblo.entities.database.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PrefManager(context: Context) {

    private val Context.dataStore by preferencesDataStore("app_preferences")
    private val dataStore = context.dataStore


    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[Keys.ID_USER_KEY] = user.idUser ?: ""
            prefs[Keys.EMAIL_KEY] = user.email ?: ""
            prefs[Keys.AVATAR_URL_KEY] = user.avatarUrl ?: ""
            prefs[Keys.NAME_KEY] = user.name
        }
    }

    suspend fun loadUser(): User {
        val prefs = dataStore.data.first()
        return User(
            prefs[Keys.ID_USER_KEY],
            prefs[Keys.NAME_KEY] ?: "",
            prefs[Keys.EMAIL_KEY],
            prefs[Keys.AVATAR_URL_KEY]
        )
    }

    suspend fun saveThemeMode(mode: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE_KEY] = mode
        }
    }

    private suspend fun loadThemeMode(): Int {
        val prefs = dataStore.data.first()
        return prefs[Keys.THEME_MODE_KEY] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun getThemeModeSync(): Int = runBlocking { loadThemeMode() }

    private object Keys {
        val ID_USER_KEY = stringPreferencesKey("idUser")
        val NAME_KEY = stringPreferencesKey("name")
        val EMAIL_KEY = stringPreferencesKey("email")
        val AVATAR_URL_KEY = stringPreferencesKey("avatarUrl")

        val THEME_MODE_KEY = intPreferencesKey("SYSTEM_MODE_KEY")
    }

}