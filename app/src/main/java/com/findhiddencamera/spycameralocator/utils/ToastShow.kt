package com.findhiddencamera.spycameralocator.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.databinding.CustomToastBinding

object ToastShow {

    fun showToast(string: String) {
        ToastUtils.make().setGravity(Gravity.BOTTOM, 0, SizeUtils.dp2px(100F)).show(string)
    }

    fun showToast(context: Context, type: ToastType, string: String) {
        val binding = CustomToastBinding.inflate(LayoutInflater.from(context))
        val icon = when(type) {
            ToastType.SUCCESS -> R.mipmap.img_toast_sub_icon
            ToastType.ERROR -> R.drawable.svg_toast_error
            else -> R.drawable.svg_toast_error
        }
        binding.toastLayout.setBackgroundResource(if (type == ToastType.SUCCESS) R.drawable.success_toast_bg else R.drawable.toast_bg)
        binding.toastIcon.setImageResource(icon)
        binding.toastText.text = string

        // 创建并显示 Toast
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.TOP, 0, 100)
        toast.view = binding.root
        toast.show()
    }

//    fun showCenterToast(context: Context, type: ToastType, string: String) {
//        val binding = CustomCenterToastBinding.inflate(LayoutInflater.from(context))
//        val icon = when(type) {
//            ToastType.SUCCESS -> R.drawable.svg_toast_success
//            ToastType.ERROR -> R.drawable.svg_toast_error
//            else -> R.drawable.svg_toast_error
//        }
//        binding.toastIcon.setImageResource(icon)
//        binding.toastText.text = string
//
//        // 创建并显示 Toast
//        val toast = Toast(context)
//        toast.duration = Toast.LENGTH_LONG
//        toast.setGravity(Gravity.TOP, 0, 100)
//        toast.view = binding.root
//        toast.show()
//    }
}

fun String.showToast(context: Context, type: ToastType = ToastType.HINT) {
    context.findBaseActivityVBind()?.runOnUiThread {
        ToastShow.showToast(context, type, this)
    }
}

fun Int.showToast(context: Context, type: ToastType = ToastType.HINT) {
    context.findBaseActivityVBind()?.runOnUiThread {
        val content = context.getString(this)
        ToastShow.showToast(context, type, content)
    }
}

enum class ToastType {
    SUCCESS, // 成功
    ERROR, // 错误
    WARNING, // 警告
    HINT // 提示
}