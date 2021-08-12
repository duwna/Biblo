package com.duwna.biblo

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.duwna.biblo.data.PrefManager
import com.duwna.biblo.di.components.AppComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val appComponent = AppComponent.Factory.create(applicationContext)
        appContext = applicationContext
        AppCompatDelegate.setDefaultNightMode(PrefManager.getThemeModeSync())
    }

    companion object {
        lateinit var appContext: Context
    }
}