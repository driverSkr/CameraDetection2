package com.findhiddencamera.spycameralocator.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataHelper {
    private const val TAG = "DataHelper"

    fun getLanguage(context: Context) : String? {
        val sharedPreferences = context.getSharedPreferences("sp_language", Context.MODE_PRIVATE)
        return sharedPreferences.getString("sp_language_data", null)
    }

    fun isFirst(context: Context, key: String) : Boolean {
        val sharedPreferences = context.getSharedPreferences("sp_first_$key", Context.MODE_PRIVATE)
        val result = sharedPreferences.getBoolean("key", true)
        if (result) {
            sharedPreferences.edit {
                putBoolean("key", false)
            }
        }
        return result
    }

    fun canShowDaily(context: Context, key: String, maxCount: Int): Boolean {
        return runCatching {
            val sharedPreferences = context.getSharedPreferences("sp_daily_$key", Context.MODE_PRIVATE)
            val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val savedDay = sharedPreferences.getString("day", null)
            val currentCount = if (savedDay == today) {
                sharedPreferences.getInt("count", 0)
            } else {
                0
            }

            if (currentCount >= maxCount) {
                Log.d(TAG, "今日$key 已展示$currentCount 次，达到上限$maxCount 次")
                false
            } else {
                // 只有确认要展示时才增加计数，避免未弹出场景误消耗次数
                sharedPreferences.edit {
                    putString("day", today)
                    putInt("count", currentCount + 1)
                }
                Log.d(TAG, "今日$key 第${currentCount + 1} 次展示，最大次数：$maxCount")
                true
            }
        }.getOrElse { throwable ->
            // 计数异常时不阻断原有展示流程，同时记录日志便于排查
            Log.e(TAG, "读取每日展示次数失败：$key", throwable)
            true
        }
    }

    fun getDailyShowCount(context: Context, key: String): Int {
        return runCatching {
            val sharedPreferences = context.getSharedPreferences("sp_daily_$key", Context.MODE_PRIVATE)
            val today = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val savedDay = sharedPreferences.getString("day", null)
            if (savedDay == today) {
                sharedPreferences.getInt("count", 0)
            } else {
                0
            }
        }.getOrElse { throwable ->
            // 读取失败时按首次展示处理，避免影响订阅页正常打开
            Log.e(TAG, "读取每日展示次数失败：$key", throwable)
            0
        }
    }
}
