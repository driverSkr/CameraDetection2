package com.findhiddencamera.spycameralocator.ui.subscribe

import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.findhiddencamera.spycameralocator.base.BaseActivityVBind
import com.findhiddencamera.spycameralocator.databinding.LayoutComposeContainerBinding
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.theme.Transparent
import com.findhiddencamera.spycameralocator.ui.result.BluetoothScanDetailActivity
import com.findhiddencamera.spycameralocator.ui.result.WifiDetectDetailActivity
import com.findhiddencamera.spycameralocator.ui.subscribe.page.SplashScreenSubscribePage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class SplashScreenSubscribeActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context) {
            context.intentOf<SplashScreenSubscribeActivity> {
                startActivity(context)
            }
        }

        fun launchForDeviceDetailAfterClose(context: Context, device: WifiDevice) {
            context.intentOf<SplashScreenSubscribeActivity> {
                +("wifiDeviceDetailAfterClose" to device)
                startActivity(context)
            }
        }

        fun launchForDeviceDetailAfterClose(context: Context, device: BluetoothDevice) {
            context.intentOf<SplashScreenSubscribeActivity> {
                +("bluetoothDeviceDetailAfterClose" to device)
                startActivity(context)
            }
        }
    }

    private val wifiDeviceDetailAfterClose by bundle<WifiDevice>("wifiDeviceDetailAfterClose")
    private val bluetoothDeviceDetailAfterClose by bundle<BluetoothDevice>("bluetoothDeviceDetailAfterClose")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (wifiDeviceDetailAfterClose != null || bluetoothDeviceDetailAfterClose != null) {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeAndOpenDeviceDetail()
                }
            })
        }
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            SplashScreenSubscribePage(
                                onClose = {
                                    closeAndOpenDeviceDetail()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun closeAndOpenDeviceDetail() {
        val wifiDevice = wifiDeviceDetailAfterClose
        val bluetoothDevice = bluetoothDeviceDetailAfterClose
        if (wifiDevice != null) {
            // 仅锁定结果入口携带该参数，关闭开屏订阅页后进入对应设备详情。
            WifiDetectDetailActivity.launch(this, wifiDevice)
        } else if (bluetoothDevice != null) {
            // 仅锁定结果入口携带该参数，关闭开屏订阅页后进入对应设备详情。
            BluetoothScanDetailActivity.launch(this, bluetoothDevice)
        }
        finish()
    }
}
