package com.duwna.biblo.utils

import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.*
import com.google.android.material.snackbar.Snackbar

fun View.circularShow() {
    ViewAnimationUtils.createCircularReveal(
        this,
        width / 2,
        height / 2,
        0f,
        maxOf(width, height) / 2f
    )
        .apply {
            duration = 500
            doOnStart { isVisible = true }
        }.start()
}

fun View.circularHide() {
    ViewAnimationUtils.createCircularReveal(
        this,
        width / 2,
        height / 2,
        maxOf(width, height) / 2f,
        0f
    )
        .apply {
            duration = 500
            doOnEnd { isVisible = false }
        }.start()
}

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) = updateLayoutParams<ViewGroup.MarginLayoutParams> {
    setMargins(left, top, right, bottom)
}

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

fun View.showSnackBar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, length).show()
}