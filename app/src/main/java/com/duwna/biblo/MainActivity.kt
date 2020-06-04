package com.duwna.biblo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duwna.biblo.ui.base.Notify
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

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
