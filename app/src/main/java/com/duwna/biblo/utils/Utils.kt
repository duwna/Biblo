package com.duwna.biblo.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.util.*


fun View.circularShow() {
    ViewAnimationUtils
        .createCircularReveal(this, width / 2, height / 2, 0f, maxOf(width, height) / 2f)
        .apply {
            duration = 500
            doOnStart { isVisible = true }
        }.start()
}

fun View.circularHide() {
    ViewAnimationUtils
        .createCircularReveal(this, width / 2, height / 2, maxOf(width, height) / 2f, 0f)
        .apply {
            duration = 500
            doOnEnd { isVisible = false }
        }.start()
}

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    )
}

fun Context.dpToIntPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

fun Context.attrValue(@AttrRes res: Int): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(res, tv, true)) tv.data
    else throw Resources.NotFoundException("Resource with id $res not found")
}

fun Context.hideKeyBoard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Date.format(pattern: String = "HH:mm dd.MM"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Any.log(msg: Any?, tag: String = this::class.java.simpleName) {
    Log.e(tag, msg.toString())
}