package com.duwna.biblo.utils

import android.util.Log
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
    length >= 2 -> substring(0, 2).uppercase(Locale.US)
    isNotBlank() -> substring(0, 1).uppercase(Locale.US)
    else -> ""
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