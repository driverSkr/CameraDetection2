package com.ethan.cameradetection2.ui.home

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
import com.ethan.cameradetection2.base.BaseActivityVBind
import com.ethan.cameradetection2.databinding.LayoutComposeContainerBinding
import com.ethan.cameradetection2.theme.ComposeProjectTheme
import com.ethan.cameradetection2.theme.Transparent
import com.ethan.cameradetection2.ui.home.page.HomePage
import com.ethan.cameradetection2.utils.WifiHelper
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