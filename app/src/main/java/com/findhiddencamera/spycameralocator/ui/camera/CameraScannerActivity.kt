package com.findhiddencamera.spycameralocator.ui.camera

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.findhiddencamera.spycameralocator.base.BaseActivityVBind
import com.findhiddencamera.spycameralocator.databinding.LayoutComposeContainerBinding
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.theme.Transparent
import com.findhiddencamera.spycameralocator.ui.camera.page.CameraScannerPage
import com.findhiddencamera.spycameralocator.ui.subscribe.SubscribeActivity
import com.findhiddencamera.spycameralocator.utils.DataHelper
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf

class CameraScannerActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context) {
            if (SubscribeHelper.isSubscribed) {
                launchActivity(context, showSubscribeGuide = false)
            } else if (DataHelper.isFirst(context, "camera_subscribe_guide")) {
                launchActivity(context, showSubscribeGuide = true)
            } else {
                SubscribeActivity.launch(context)
            }
        }

        private fun launchActivity(context: Context, showSubscribeGuide: Boolean) {
            context.intentOf<CameraScannerActivity> {
                +("showSubscribeGuide" to showSubscribeGuide)
                startActivity(context)
            }
        }
    }

    private val showSubscribeGuide by bundle<Boolean>("showSubscribeGuide")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            CameraScannerPage(showSubscribeGuide = showSubscribeGuide == true)
                        }
                    }
                }
            }
        }
    }
}
