package com.duwna.biblo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
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

fun Context.toast(msg: Any?, tag: String = this::class.java.simpleName) {
    Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show()
}

fun String.toInitials(): String = when {
    length >= 2 -> substring(0, 2).toUpperCase(Locale.US)
    isNotBlank() -> substring(0, 1).toUpperCase(Locale.US)
    else -> ""
}

fun Fragment.pickImageFromGallery() {
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }
    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
    startActivityForResult(gallery, PICK_IMAGE_CODE)
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

const val PERMISSION_REQUEST_CODE = 200
const val PICK_IMAGE_CODE = 100