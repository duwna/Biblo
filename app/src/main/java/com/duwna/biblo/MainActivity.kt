package com.duwna.biblo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import com.duwna.biblo.R
import com.duwna.biblo.base.Notify
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
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
}
