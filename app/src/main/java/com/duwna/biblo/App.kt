package com.duwna.biblo

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.duwna.biblo.data.PrefManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        AppCompatDelegate.setDefaultNightMode(PrefManager.getThemeModeSync())
    }

    companion object {
        lateinit var appContext: Context
    }
}