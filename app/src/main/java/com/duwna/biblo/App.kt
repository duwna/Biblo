package com.duwna.biblo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.duwna.biblo.data.PrefManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var prefs: PrefManager

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(prefs.getThemeModeSync())
    }
}