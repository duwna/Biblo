package com.duwna.biblo.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String = "HH:mm, dd.MM"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Any.log(msg: Any?, tag: String = this::class.java.simpleName) {
    Log.e(tag, msg.toString())
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

fun Double.format(digits: Int = 2) = "%.${digits}f".format(Locale.US, this)

fun Double.equalsDelta(other: Double) = kotlin.math.abs(this / other - 1) < 0.01

fun randomID() = UUID.randomUUID().toString()

fun <T> tryOrNull(block: () -> T?): T? = try {
    block()
} catch (t: Throwable) {
    null
}

const val PERMISSION_REQUEST_CODE = 200
const val PICK_IMAGE_CODE = 100