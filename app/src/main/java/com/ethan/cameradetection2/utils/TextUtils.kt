package com.ethan.cameradetection2.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.forEach
import kotlin.collections.indices
import kotlin.collections.isEmpty
import kotlin.sequences.map
import kotlin.sequences.toList
import kotlin.text.lastIndexOf


object TextUtils {

    fun setSpanText(context: Context, content: String, spans: Array<Span>, textView: TextView) {
        if (spans.isEmpty()) {
            return
        }
        try {
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(content)
            for (i in spans.indices) {
                val span = spans[i]
                val foregroundColorSpan = ForegroundColorSpan(context.resources.getColor(span.color))
                val clickableSpan = span.click
                val spanText = span.text
                val styleSpan = StyleSpan(Typeface.BOLD)
                val index = content.lastIndexOf(spanText)
                spannableStringBuilder.setSpan(clickableSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(foregroundColorSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(styleSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.text = spannableStringBuilder
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun setAllSpanText(context: Context, content: String, spans: Array<Span>, textView: TextView) {
        if (spans.isEmpty()) {
            return
        }
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(content)
        for (i in spans.indices) {
            val span = spans[i]
            val spanText = span.text
            val indexList = findAllMatches(content, spanText)
            indexList.forEach { index ->
                val foregroundColorSpan = ForegroundColorSpan(context.resources.getColor(span.color))
                val styleSpan = StyleSpan(Typeface.BOLD)
                val clickableSpan = span.click
                spannableStringBuilder.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            clickableSpan.onClick(widget)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false // 取消下划线
                        }
                    }, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                //spannableStringBuilder.setSpan(clickableSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(foregroundColorSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(styleSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = spannableStringBuilder
    }

    data class Span(val text: String, val color: Int, val click: ClickableSpan)

    private fun findAllMatches(text: String, pattern: String): List<Int> {
        val regex = Regex(pattern)
        val matches = regex.findAll(text)
        return matches.map { it.range.first }.toList()
    }

    private fun isToday(zonedDateTime: ZonedDateTime, now: ZonedDateTime): Boolean {
        return zonedDateTime.year == now.year &&
                zonedDateTime.month == now.month &&
                zonedDateTime.dayOfMonth == now.dayOfMonth
    }

    private fun isYesterday(zonedDateTime: ZonedDateTime, now: ZonedDateTime): Boolean {
        val yesterday = now.minusDays(1)
        return zonedDateTime.year == yesterday.year &&
                zonedDateTime.month == yesterday.month &&
                zonedDateTime.dayOfMonth == yesterday.dayOfMonth
    }

    fun formatTime(context: Context, time: Long?): String? {
        if (time == null || time == 0L) return null

        val instant = Instant.ofEpochSecond(time)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault()) // 使用系统默认时区
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val formatterToday = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        val formatterOther = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.getDefault())
        val formatterYesterday = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

        return when {
            isToday(zonedDateTime, now) -> "今天 ${zonedDateTime.format(formatterToday)}"
            isYesterday(zonedDateTime, now) -> "昨天 ${zonedDateTime.format(formatterYesterday)}"
            else -> zonedDateTime.format(formatterOther)
        }
    }

    fun formatTimeHhMm(time: Long?): String? {
        if (time == null || time == 0L) return null

        val instant = Instant.ofEpochSecond(time)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault()) // 使用系统默认时区
        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val formatterToday = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

        return zonedDateTime.format(formatterToday)
    }
}