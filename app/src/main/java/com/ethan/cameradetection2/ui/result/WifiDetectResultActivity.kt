package com.ethan.cameradetection2.ui.result

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.ethan.cameradetection2.base.BaseActivityVBind
import com.ethan.cameradetection2.databinding.LayoutComposeContainerBinding
import com.ethan.cameradetection2.model.WifiDevice
import com.ethan.cameradetection2.theme.ComposeProjectTheme
import com.ethan.cameradetection2.theme.Transparent
import com.ethan.cameradetection2.ui.result.page.WifiDetectResultPage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class WifiDetectResultActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, suspiciousDevices: List<WifiDevice>, trustedDevices: List<WifiDevice>) {
            context.intentOf<WifiDetectResultActivity> {
                +("suspiciousDevices" to suspiciousDevices)
                +("trustedDevices" to trustedDevices)
                startActivity(context)
            }
        }
    }

    private val suspiciousDevices by bundle<ArrayList<WifiDevice>>("suspiciousDevices")
    private val trustedDevices by bundle<ArrayList<WifiDevice>>("trustedDevices")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            WifiDetectResultPage(suspiciousDevices, trustedDevices)
                        }
                    }
                }
            }
        }
    }
}