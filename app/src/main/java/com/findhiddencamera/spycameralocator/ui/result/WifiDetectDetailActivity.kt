package com.findhiddencamera.spycameralocator.ui.result

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.findhiddencamera.spycameralocator.base.BaseActivityVBind
import com.findhiddencamera.spycameralocator.databinding.LayoutComposeContainerBinding
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.theme.Transparent
import com.findhiddencamera.spycameralocator.ui.result.page.WifiDetectDetailPage
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class WifiDetectDetailActivity: BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context, device: WifiDevice) {
            context.intentOf<WifiDetectDetailActivity> {
                +("device" to device)
                startActivity(context)
            }
        }
    }

    private val device by bundle<WifiDevice>("device")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            WifiDetectDetailPage(device)
                        }
                    }
                }
            }
        }
    }
}