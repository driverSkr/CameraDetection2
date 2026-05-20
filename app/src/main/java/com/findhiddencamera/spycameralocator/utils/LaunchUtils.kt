package com.findhiddencamera.spycameralocator.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.findhiddencamera.spycameralocator.R

object LaunchUtils {
    fun launchWeb(context: Context?, url: String, title: String) {
        try {
            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Log.d("ethan", "e:${e.printStackTrace()}")
        }
    }

    fun launchPlayStore(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            Log.d("ethan", "包名:${context.packageName}")
            intent.data = Uri.parse("market://details?id=" + context.packageName)
            intent.setPackage("com.android.vending")
            context.startActivity(intent)
        } catch (e: Exception) {
            launchWeb(context, "https://play.google.com/store/apps/details?id=" + context.packageName, context.getString(R.string.app_name))
        }
    }

    fun launchSubscriptionManage(context: Context, productId: String) {
        val subscriptionUri = Uri.parse("https://play.google.com/store/account/subscriptions?sku=$productId&package=${context.packageName}")
        try {
            // 优先拉起 Google Play 订阅管理页，方便用户直接管理当前订阅商品。
            val intent = Intent(Intent.ACTION_VIEW, subscriptionUri)
            intent.setPackage("com.android.vending")
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("LaunchUtils", "打开Google Play订阅管理页失败", e)
            launchWeb(context, subscriptionUri.toString(), context.getString(R.string.app_name))
        }
    }
}
