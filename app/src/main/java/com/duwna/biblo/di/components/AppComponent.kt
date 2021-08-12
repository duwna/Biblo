package com.duwna.biblo.di.components

import android.content.Context
import com.duwna.biblo.App
import dagger.BindsInstance
import dagger.Component
import com.duwna.biblo.di.modules.PreferencesModule
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferencesModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(app: App)
}