package com.duwna.biblo

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.duwna.biblo.data.PrefManager
import kotlinx.coroutines.runBlocking

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val mode = runBlocking { PrefManager.loadThemeMode() }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        lateinit var appContext: Context
    }
}