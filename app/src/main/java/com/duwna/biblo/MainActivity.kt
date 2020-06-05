package com.duwna.biblo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.duwna.biblo.ui.base.Notify
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupNavigation()
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_auth, R.id.navigation_groups)
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG)
        when (notify) {
            is Notify.InternetError -> snackbar.duration = Snackbar.LENGTH_SHORT
            is Notify.ActionMessage ->
                snackbar.setAction(notify.actionLabel) { notify.actionHandler.invoke() }
        }
        snackbar.show()
    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp()
//    }
}
