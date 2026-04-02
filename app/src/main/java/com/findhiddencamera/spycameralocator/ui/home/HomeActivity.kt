package com.findhiddencamera.spycameralocator.ui.home

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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
import com.findhiddencamera.spycameralocator.utils.WifiHelper
import com.skydoves.bundler.intentOf

class HomeActivity : BaseActivityVBind<LayoutComposeContainerBinding>() {

    private val wifiPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && (Build.VERSION.SDK_INT < 33 || permissions[Manifest.permission.NEARBY_WIFI_DEVICES] == true)
        if (granted) {
            Toast.makeText(this, "权限或得成功", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun launch(context: Context) {
            context.intentOf<HomeActivity> {
                startActivity(context)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WifiHelper.checkWifiPermission(this, wifiPermissionLauncher)
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