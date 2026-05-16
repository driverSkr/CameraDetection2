package com.findhiddencamera.spycameralocator.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.findhiddencamera.spycameralocator.dialog.view.RequestBluetoothPermissionView
import com.findhiddencamera.spycameralocator.dialog.view.RequestWifiPermissionView
import com.findhiddencamera.spycameralocator.dialog.view.WifiInfoDetailsView
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.utils.ComposeNativeDialog

object DialogHelper {
    fun showWifiInfoDialog(activity: FragmentActivity, device: WifiDevice) {
        val (binding, dialog) = ComposeNativeDialog.composeBottomDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        WifiInfoDetailsView(dialog, device)
                        Spacer(modifier = Modifier.height(42.dp))
                    }
                }
            }
        }
        dialog.show()
    }

    fun requestWifiPermissionDialog(activity: FragmentActivity) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    RequestWifiPermissionView()
                }
            }
        }
        dialog.show()
    }

    fun requestBluetoothPermissionDialog(activity: FragmentActivity) {
        val (binding, dialog) = ComposeNativeDialog.composeBaseDialog(activity)
        binding.composeView.apply {
            setContent {
                ComposeProjectTheme {
                    RequestBluetoothPermissionView()
                }
            }
        }
        dialog.show()
    }
}