package com.duwna.biblo.di.modules

import android.content.Context
import com.duwna.biblo.data.PrefManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object PreferencesModule {
    @Provides
    @Singleton
    fun providePrefManager(context: Context): PrefManager = PrefManager(context)
}