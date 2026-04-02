package com.findhiddencamera.spycameralocator.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.round
import kotlin.text.format
import kotlin.text.split
import kotlin.text.toFloat

/** 时分秒 00:00:00 */
@SuppressLint("DefaultLocale")
fun Long.formatHMSTime(): String {
    val seconds = (this / 1000) % 60
    val minutes = (this / (1000 * 60)) % 60
    val hours = ((this / (1000 * 60)) % 60) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

/**
 * 时分秒 00:00:00
 *  分秒 00:00
 * */
@SuppressLint("DefaultLocale")
fun Long.formatHMSTime2(): String {
    val totalSeconds = round(this / 1000.0).toLong()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * 时间小于等于0时返回空字符串
 * 时分秒 00:00:00
 *  分秒 00:00
 * */
@SuppressLint("DefaultLocale")
fun Float.formatHMSTime3(): String {
    if(this == 0f) return ""
    val totalSeconds = round(this / 1000.0).toLong()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * 时分秒 00:00:00
 *  分秒 00:00
 * */
@SuppressLint("DefaultLocale")
fun Float.formatHMSTime4(): String {
    val totalSeconds = ceil(this).toLong()
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

/** 分秒毫秒 00:00:00 */
@SuppressLint("DefaultLocale")
fun Long.formatMSCTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60          // 计算分钟
    val seconds = totalSeconds % 60          // 计算秒
    val centiSeconds = (this % 1000) / 10  // 取毫秒的前两位（即十毫秒）

    // 格式化为 "00:00:00" 格式（分钟:秒:十毫秒）
    return String.format("%02d:%02d:%02d", minutes, seconds, centiSeconds)
}

/** 时分秒毫秒 00:00:00:000 */
fun Long.formatHMSCTime(): String {
    return if (this > 0) {
        val totalSeconds = this / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val millis = this % 1000
        "$hours:$minutes:$seconds.$millis"
    } else {
        "00:00:00.000"
    }
}

/**
 * 时间戳转化时间格式 2025-11-04
 */
fun Long.timestampToDate(): String {
    return try {
        val instant = Instant.ofEpochSecond(this)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dateTime.format(formatter)
    } catch (_: Exception) {
        this.toString()
    }
}

fun Int.timestampToDate(): String {
    return try {
        val instant = Instant.ofEpochSecond(this.toLong())
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dateTime.format(formatter)
    } catch (_: Exception) {
        this.toString()
    }
}

fun Int.timestampToDateMM(): String {
    return try {
        val instant = Instant.ofEpochSecond(this.toLong())
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        dateTime.format(formatter)
    } catch (_: Exception) {
        this.toString()
    }
}

fun Int.timestampToDateSS(): String {
    return try {
        val instant = Instant.ofEpochSecond(this.toLong())
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        dateTime.format(formatter)
    } catch (_: Exception) {
        this.toString()
    }
}

fun Int.timestampToDateSS2(): String {
    return try {
        val instant = Instant.ofEpochSecond(this.toLong())
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
        dateTime.format(formatter)
    } catch (_: Exception) {
        this.toString()
    }
}

fun Long.formatSecondTime(): Long = round(this / 1000.0).toLong()

fun getAudioName(): String {
    val dateFormat = SimpleDateFormat("HHmmss_dd_MM_yyyy", Locale.getDefault())
    return "Audio_${dateFormat.format(Date())}.m4a"
}
fun getAudioName(ext: String): String {
    val dateFormat = SimpleDateFormat("HHmmss_dd_MM_yyyy", Locale.getDefault())
    return "Audio_${dateFormat.format(Date())}.$ext"
}

fun timestampToDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(System.currentTimeMillis()))
}

// "3:4" -> 0.75f
fun String.toAspectRatio(): Float {
    return try {
        val (width, height) = this.split(":")
        width.toFloat() / height.toFloat()
    } catch (_: Exception) {
        1f // 默认宽高比
    }
}

/**
 * 1: version1 > version2
 * -1: version1 < version2
 * 0: version1 == version2
 * */
//fun compareVersions(version1: String, version2: String): Int {
//    val parts1 = version1.split(".").map { it.toInt() }
//    val parts2 = version2.split(".").map { it.toInt() }
//
//    val maxLength = kotlin.comparisons.maxOf(parts1.size, parts2.size)
//
//    for (i in 0 until maxLength) {
//        val part1 = parts1.getOrElse(i) { 0 }
//        val part2 = parts2.getOrElse(i) { 0 }
//
//        when {
//            part1 > part2 -> return 1
//            part1 < part2 -> return -1
//        }
//    }
//    return 0
//}

/**
 * 获取今天0点的秒级时间戳
 */
fun getTodayStartSeconds(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis / 1000  // 转为秒级
}