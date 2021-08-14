package com.duwna.biblo.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String = "HH:mm, dd.MM"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "HH:mm, dd.MM"
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(this)
}

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
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

fun FirebaseAuth.userId() = currentUser!!.uid