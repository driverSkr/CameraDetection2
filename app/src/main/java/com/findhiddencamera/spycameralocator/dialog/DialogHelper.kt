package com.findhiddencamera.spycameralocator.dialog

import androidx.fragment.app.FragmentActivity
import com.findhiddencamera.spycameralocator.dialog.view.GuideCheckView
import com.findhiddencamera.spycameralocator.dialog.view.RequestBluetoothPermissionView
import com.findhiddencamera.spycameralocator.dialog.view.RequestWifiPermissionView
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.utils.ComposeNativeDialog

object DialogHelper {

    fun requestWifiPermissionDialog(
        activity: FragmentActivity,
        onAllow: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    RequestWifiPermissionView(dialog, onAllow, onCancel)
                }
            }
        }
        dialog.show()
    }

    fun requestBluetoothPermissionDialog(
        activity: FragmentActivity,
        onAllow: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    RequestBluetoothPermissionView(dialog, onAllow, onCancel)
                }
            }
        }
        dialog.show()
    }

    fun guideCheckDialog(
        activity: FragmentActivity,
        content: String = "Hide & Spy Cameras stream your video to nearby devices via Bluetooth, so be sure to check as soon as possible.",
        imageRes: Int = com.findhiddencamera.spycameralocator.R.mipmap.img_bluetooth_radar_plate,
        onStart: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    GuideCheckView(dialog, content, imageRes, onStart, onCancel)
                }
            }
        }
        dialog.show()
    }
}
