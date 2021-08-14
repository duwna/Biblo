package com.duwna.biblo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_auth, R.id.navigation_groups)
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    fun renderNotification(notify: Notify) {
        when (notify) {
            is Notify.InternetError -> container.showSnackBar(getString(R.string.message_internet_error))
            is Notify.DataError -> container.showSnackBar(getString(R.string.message_data_error))
            is Notify.MessageFromRes -> container.showSnackBar(getString(notify.resId))
            is Notify.TextMessage -> container.showSnackBar(notify.message)
        }
    }

    fun renderLoading(isLoading: Boolean) {
        progress_circular.isVisible = isLoading
    }
}
