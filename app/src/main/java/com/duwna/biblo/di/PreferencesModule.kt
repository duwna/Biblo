package com.duwna.biblo.di

import android.content.Context
import com.duwna.biblo.data.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun providePrefManager(@ApplicationContext context: Context): PrefManager = PrefManager(context)
}