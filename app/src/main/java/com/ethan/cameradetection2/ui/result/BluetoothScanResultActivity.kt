package com.ethan.cameradetection2.ui.result

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.ethan.cameradetection2.base.BaseActivityVBind
import com.ethan.cameradetection2.databinding.LayoutComposeContainerBinding
import com.ethan.cameradetection2.model.BluetoothDevice
import com.ethan.cameradetection2.model.WifiDevice
import com.ethan.cameradetection2.theme.ComposeProjectTheme
import com.ethan.cameradetection2.theme.Transparent
import com.ethan.cameradetection2.ui.result.page.BluetoothScanResultPage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf
import kotlin.getValue

class BluetoothScanResultActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, suspiciousDevices: List<BluetoothDevice>, trustedDevices: List<BluetoothDevice>) {
            context.intentOf<BluetoothScanResultActivity> {
                +("suspiciousDevices" to suspiciousDevices)
                +("trustedDevices" to trustedDevices)
                startActivity(context)
            }
        }
    }

    private val suspiciousDevices by bundle<ArrayList<BluetoothDevice>>("suspiciousDevices")
    private val trustedDevices by bundle<ArrayList<BluetoothDevice>>("trustedDevices")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            BluetoothScanResultPage(suspiciousDevices, trustedDevices)
                        }
                    }
                }
            }
        }
    }
}