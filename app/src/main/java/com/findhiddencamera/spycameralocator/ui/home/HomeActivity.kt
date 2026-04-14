package com.findhiddencamera.spycameralocator.ui.home

import android.content.Context
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.findhiddencamera.spycameralocator.base.BaseActivityVBind
import com.findhiddencamera.spycameralocator.databinding.LayoutComposeContainerBinding
import com.findhiddencamera.spycameralocator.theme.ComposeProjectTheme
import com.findhiddencamera.spycameralocator.theme.Transparent
import com.findhiddencamera.spycameralocator.ui.home.page.HomePage
import com.findhiddencamera.spycameralocator.utils.AppPermissionHelper
import com.skydoves.bundler.intentOf

class HomeActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    private val homePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    companion object {
        fun launch(context: Context) {
            context.intentOf<HomeActivity> {
                startActivity(context)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppPermissionHelper.requestHomePermissionsIfNeeded(this, homePermissionLauncher)
        binding.composeView.apply {
            setContent {
                CompositionLocalProvider {
                    ComposeProjectTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Transparent) {
                            HomePage()
                        }
                    }
                }
            }
        }
    }
}

