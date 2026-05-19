package com.findhiddencamera.spycameralocator.dialog

import androidx.fragment.app.FragmentActivity
import com.findhiddencamera.spycameralocator.dialog.view.RequestBluetoothPermissionView
import com.findhiddencamera.spycameralocator.dialog.view.RequestWifiPermissionView
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.utils.ComposeNativeDialog

object DialogHelper {

    fun requestWifiPermissionDialog(activity: FragmentActivity, onAllow: () -> Unit = {}) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    RequestWifiPermissionView(dialog, onAllow)
                }
            }
        }
        dialog.show()
    }

    fun requestBluetoothPermissionDialog(activity: FragmentActivity, onAllow: () -> Unit = {}) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    RequestBluetoothPermissionView(dialog, onAllow)
                }
            }
        }
        dialog.show()
    }
}
