package com.example.sikrepmus.util

import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.toFormattedDuration(): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun Int.toFormattedDuration(): String {
    val minutes = this / 60
    val seconds = this % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
