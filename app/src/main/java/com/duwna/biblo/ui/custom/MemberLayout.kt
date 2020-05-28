package com.duwna.biblo.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.isVisible
import com.duwna.biblo.R
import kotlinx.android.synthetic.main.item_add_member.view.*

class MemberLayout(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {

    init {
        orientation = VERTICAL
    }

    fun addMember() {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_add_member, this, false)

        view.apply {
            addMemberView(this)
            iv_delete.setOnClickListener {
                deleteMemberView(this)
            }
        }
    }

    fun getMemberNamesList(): List<String> = mutableListOf<String>().apply {
        children.forEach { add(it.et_name.text.toString()) }
    }

    private fun addMemberView(view: View) {
        addView(view)
        ViewAnimationUtils
            .createCircularReveal(view, width / 2, height / 2, 0f, maxOf(width, height) / 2f)
            .apply {
                duration = 500
                doOnStart { isVisible = true }
            }.start()
    }

    private fun deleteMemberView(view: View) {
        ViewAnimationUtils
            .createCircularReveal(view, width / 2, height / 2, maxOf(width, height) / 2f, 0f)
            .apply {
                duration = 500
                doOnEnd { removeView(view) }
            }.start()
    }


}