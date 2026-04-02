package com.findhiddencamera.spycameralocator.ui.start

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
import com.findhiddencamera.spycameralocator.ui.start.page.PolicyPage
import com.skydoves.bundler.intentOf

class PolicyActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    companion object {
        fun launch(context: Context) {
            context.intentOf<PolicyActivity> {
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
                            PolicyPage()
                        }
                    }
                }
            }
        }
    }
}