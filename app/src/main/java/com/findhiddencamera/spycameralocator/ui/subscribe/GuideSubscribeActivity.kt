package com.findhiddencamera.spycameralocator.ui.subscribe

import android.content.Context
import android.os.Bundle
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
import com.findhiddencamera.spycameralocator.ui.subscribe.page.GuideSubscribePage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class GuideSubscribeActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        private const val RESULT_TYPE_WIFI = "wifi"
        private const val RESULT_TYPE_BLUETOOTH = "bluetooth"

        fun launchForWifi(
            context: Context,
            suspiciousDevices: List<WifiDevice>,
            trustedDevices: List<WifiDevice>,
            scanTimeSeconds: Long
        ) {
            context.intentOf<GuideSubscribeActivity> {
                +("resultType" to RESULT_TYPE_WIFI)
                +("wifiSuspiciousDevices" to suspiciousDevices)
                +("wifiTrustedDevices" to trustedDevices)
                +("scanTimeSeconds" to scanTimeSeconds)
                startActivity(context)
            }
        }

        fun launchForBluetooth(
            context: Context,
            suspiciousDevices: List<BluetoothDevice>,
            trustedDevices: List<BluetoothDevice>,
            scanTimeSeconds: Long
        ) {
            context.intentOf<GuideSubscribeActivity> {
                +("resultType" to RESULT_TYPE_BLUETOOTH)
                +("bluetoothSuspiciousDevices" to suspiciousDevices)
                +("bluetoothTrustedDevices" to trustedDevices)
                +("scanTimeSeconds" to scanTimeSeconds)
                startActivity(context)
            }
        }
    }

    private val resultType by bundle<String>("resultType")
    private val wifiSuspiciousDevices by bundle<ArrayList<WifiDevice>>("wifiSuspiciousDevices")
    private val wifiTrustedDevices by bundle<ArrayList<WifiDevice>>("wifiTrustedDevices")
    private val bluetoothSuspiciousDevices by bundle<ArrayList<BluetoothDevice>>("bluetoothSuspiciousDevices")
    private val bluetoothTrustedDevices by bundle<ArrayList<BluetoothDevice>>("bluetoothTrustedDevices")
    private val scanTimeSeconds by bundle<Long>("scanTimeSeconds")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            GuideSubscribePage(
                                resultType = resultType ?: RESULT_TYPE_WIFI,
                                wifiSuspiciousDevices = wifiSuspiciousDevices,
                                wifiTrustedDevices = wifiTrustedDevices,
                                bluetoothSuspiciousDevices = bluetoothSuspiciousDevices,
                                bluetoothTrustedDevices = bluetoothTrustedDevices,
                                scanTimeSeconds = scanTimeSeconds ?: System.currentTimeMillis().div(1000)
                            )
                        }
                    }
                }
            }
        }
    }
}
