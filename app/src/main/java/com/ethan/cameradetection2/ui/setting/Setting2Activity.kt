package com.ethan.cameradetection2.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.ethan.cameradetection2.base.BaseActivityVBind
import com.ethan.cameradetection2.databinding.LayoutComposeContainerBinding
import com.ethan.cameradetection2.theme.ComposeProjectTheme
import com.ethan.cameradetection2.theme.Transparent
import com.ethan.cameradetection2.ui.setting.page.Setting2Page
import com.skydoves.bundler.intentOf

class Setting2Activity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context) {
            context.intentOf<Setting2Activity> {
                startActivity(context)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            Setting2Page()
                        }
                    }
                }
            }
        }
    }
}