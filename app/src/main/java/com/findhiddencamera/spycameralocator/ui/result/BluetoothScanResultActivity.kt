package com.findhiddencamera.spycameralocator.ui.result

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.findhiddencamera.spycameralocator.base.BaseActivityVBind
import com.findhiddencamera.spycameralocator.databinding.LayoutComposeContainerBinding
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.theme.Transparent
import com.findhiddencamera.spycameralocator.ui.result.page.BluetoothScanResultPage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class BluetoothScanResultActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(
            context: Context,
            suspiciousDevices: List<BluetoothDevice>,
            trustedDevices: List<BluetoothDevice>,
            scanTimeSeconds: Long = System.currentTimeMillis().div(1000)
        ) {
            context.intentOf<BluetoothScanResultActivity> {
                +("suspiciousDevices" to suspiciousDevices)
                +("trustedDevices" to trustedDevices)
                +("scanTimeSeconds" to scanTimeSeconds)
                startActivity(context)
            }
        }
    }

    private val suspiciousDevices by bundle<ArrayList<BluetoothDevice>>("suspiciousDevices")
    private val trustedDevices by bundle<ArrayList<BluetoothDevice>>("trustedDevices")
    private val scanTimeSeconds by bundle<Long>("scanTimeSeconds")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            BluetoothScanResultPage(
                                suspiciousDevices,
                                trustedDevices,
                                scanTimeSeconds ?: System.currentTimeMillis().div(1000)
                            )
                        }
                    }
                }
            }
        }
    }
}
