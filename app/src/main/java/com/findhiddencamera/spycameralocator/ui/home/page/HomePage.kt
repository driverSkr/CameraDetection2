package com.findhiddencamera.spycameralocator.ui.home.page

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.findhiddencamera.spycameralocator.BuildConfig
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.dialog.DialogHelper
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.theme.White50
import com.findhiddencamera.spycameralocator.ui.bluetooth.BluetoothCamerasActivity
import com.findhiddencamera.spycameralocator.ui.camera.CameraScannerActivity
import com.findhiddencamera.spycameralocator.ui.history.HistoryRecordActivity
import com.findhiddencamera.spycameralocator.ui.magnetic.MagneticFieldActivity
import com.findhiddencamera.spycameralocator.ui.setting.SettingActivity
import com.findhiddencamera.spycameralocator.ui.subscribe.SplashScreenSubscribeActivity
import com.findhiddencamera.spycameralocator.ui.subscribe.SubscribeActivity
import com.findhiddencamera.spycameralocator.ui.wifi.WiFiCamerasActivity
import com.findhiddencamera.spycameralocator.utils.AppPermissionHelper
import com.findhiddencamera.spycameralocator.utils.BluetoothHelper
import com.findhiddencamera.spycameralocator.utils.DataHelper
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.WifiHelper
import com.findhiddencamera.spycameralocator.utils.findActivity

private enum class FeatureAction {
    WIFI,
    BLUETOOTH,
    CAMERA
}

@Composable
fun HomePage() {
    val context = LocalContext.current
    val activity = context.findActivity()
    val fragmentActivity = activity as? FragmentActivity
    val isFirstHomeVisit = remember { DataHelper.isFirst(context, "enter_home_page") }
    var pendingAction by remember { mutableStateOf<FeatureAction?>(null) }
    val detectNowColor = if (isFirstHomeVisit) Color(0xFFF53863) else Color(0xFF5672FF)
    val isSubscribed by SubscribeHelper.isSubscribedFlow.collectAsState()

    fun launchWifiFromHome() {
        if (isSubscribed || DataHelper.isFirst(context, "wifi_free_scan")) {
            WiFiCamerasActivity.launch(context)
        } else {
            SubscribeActivity.launch(context)
        }
    }

    fun launchBluetoothFromHome() {
        if (isSubscribed || DataHelper.isFirst(context, "bluetooth_free_scan")) {
            BluetoothCamerasActivity.launch(context)
        } else {
            SubscribeActivity.launch(context)
        }
    }

    lateinit var checkWifiPermissionAndLaunch: () -> Unit
    lateinit var checkBluetoothPermissionAndLaunch: () -> Unit
    lateinit var openWifiSettingsForScan: () -> Unit
    lateinit var openBluetoothSettingsForScan: () -> Unit

    val wifiPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (pendingAction == FeatureAction.WIFI) {
            if (WifiHelper.hasWifiPermission(context)) {
                launchWifiFromHome()
            } else if (activity != null && AppPermissionHelper.shouldOpenSettings(activity, WifiHelper.requiredPermissions())) {
                AppPermissionHelper.openAppPermissionSettings(activity)
            }
        }
        pendingAction = null
    }

    val wifiEnableLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (pendingAction == FeatureAction.WIFI) {
            if (WifiHelper.isWifiEnabled(context)) {
                checkWifiPermissionAndLaunch()
            } else {
                pendingAction = null
            }
        }
    }

    val bluetoothEnableLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (pendingAction == FeatureAction.BLUETOOTH) {
            if (BluetoothHelper.isBluetoothEnabled(context)) {
                checkBluetoothPermissionAndLaunch()
            } else {
                pendingAction = null
            }
        }
    }

    checkWifiPermissionAndLaunch = {
        if (WifiHelper.hasWifiPermission(context)) {
            launchWifiFromHome()
        } else if (activity != null) {
            pendingAction = FeatureAction.WIFI
            AppPermissionHelper.requestPermissionsOrOpenSettings(
                activity,
                WifiHelper.requiredPermissions(),
                wifiPermissionLauncher
            )
        }
    }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (pendingAction == FeatureAction.BLUETOOTH) {
            if (BluetoothHelper.hasBluetoothPermission(context)) {
                launchBluetoothFromHome()
            } else if (activity != null && AppPermissionHelper.shouldOpenSettings(activity, BluetoothHelper.requiredPermissions())) {
                AppPermissionHelper.openAppPermissionSettings(activity)
            }
        }
        pendingAction = null
    }

    checkBluetoothPermissionAndLaunch = {
        if (BluetoothHelper.hasBluetoothPermission(context)) {
            launchBluetoothFromHome()
        } else if (activity != null) {
            pendingAction = FeatureAction.BLUETOOTH
            AppPermissionHelper.requestPermissionsOrOpenSettings(
                activity,
                BluetoothHelper.requiredPermissions(),
                bluetoothPermissionLauncher
            )
        }
    }

    openWifiSettingsForScan = {
        pendingAction = FeatureAction.WIFI
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_WIFI)
        } else {
            Intent(Settings.ACTION_WIFI_SETTINGS)
        }
        wifiEnableLauncher.launch(intent)
    }

    openBluetoothSettingsForScan = {
        pendingAction = FeatureAction.BLUETOOTH
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        } else {
            @Suppress("DEPRECATION")
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        }
        bluetoothEnableLauncher.launch(intent)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val cameraPermission = arrayOf(Manifest.permission.CAMERA)
        if (pendingAction == FeatureAction.CAMERA) {
            if (AppPermissionHelper.hasPermissions(context, cameraPermission)) {
                CameraScannerActivity.launch(context)
            } else if (activity != null && AppPermissionHelper.shouldOpenSettings(activity, cameraPermission)) {
                AppPermissionHelper.openAppPermissionSettings(activity)
            }
        }
        pendingAction = null
    }

    LaunchedEffect(Unit) {
        // 开屏订阅每天最多展示6次，超过后当天不再弹出
        if (DataHelper.canShowDaily(context, "splash_screen_subscribe", 6)) {
            SplashScreenSubscribeActivity.launch(context)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.mipmap.img_home_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = 15.dp, end = 15.dp, top = 12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Spy Camera Locator", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.mipmap.img_pro),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .width(61.dp)
                        .height(26.dp)
                        .clickable {
                            SubscribeActivity.launch(context)
                        }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Image(painter = painterResource(R.drawable.svg_settings), contentDescription = null, modifier = Modifier.clickable {
                    SettingActivity.launch(context)
                })
            }

            if (BuildConfig.DEBUG) {
                // TODO 在发布审核前移除该临时订阅开关。
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .background(color = Color(0xFFF53863), shape = RoundedCornerShape(8.dp))
                        .clickable {
                            SubscribeHelper.updateSubscribeState(!isSubscribed)
                            Toast.makeText(
                                context,
                                "Debug subscription: ${if (!isSubscribed) "ON" else "OFF"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "DEBUG 订阅状态: ${if (isSubscribed) "ON" else "OFF"}",
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 15.dp,
                contentPadding = PaddingValues(bottom = 15.dp)
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!WifiHelper.isWifiEnabled(context)) {
                                if (fragmentActivity != null) {
                                    DialogHelper.requestWifiPermissionDialog(fragmentActivity) {
                                        openWifiSettingsForScan()
                                    }
                                }
                                return@clickable
                            }
                            checkWifiPermissionAndLaunch()
                        }) {
                        Image(
                            painter = painterResource(R.mipmap.img_home_func_bg),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 120.dp)) {
                            Text("WiFi Camera", fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
                            Text(
                                "These cameras upload the recorded video to the Internet via Wi-Fi.",
                                fontSize = 12.sp,
                                color = White50,
                                lineHeight = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 15.dp, bottom = 15.dp)
                                .background(color = White, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text("Detect Now", color = detectNowColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!BluetoothHelper.isBluetoothEnabled(context)) {
                                if (fragmentActivity != null) {
                                    DialogHelper.requestBluetoothPermissionDialog(fragmentActivity) {
                                        openBluetoothSettingsForScan()
                                    }
                                }
                                return@clickable
                            }
                            checkBluetoothPermissionAndLaunch()
                        }) {
                        Image(
                            painter = painterResource(R.mipmap.img_home_func_bg),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp, end = 120.dp)) {
                            Text("Bluetooth Camera", fontSize = 18.sp, color = White, fontWeight = FontWeight.Bold)
                            Text(
                                "These cameras upload the recorded video to a nearby storage device via Bluetooth.",
                                fontSize = 12.sp,
                                color = White50,
                                lineHeight = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 15.dp, bottom = 15.dp)
                                .background(color = White, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text("Detect Now", color = detectNowColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(color = White, shape = RoundedCornerShape(10.dp))
                        .clickable {
                            MagneticFieldActivity.launch(context)
                        }) {
                        Column(modifier = Modifier.padding(top = 20.dp, start = 15.dp)) {
                            Text("Magnetic Field", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text("Abnormal signal", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                        }
                        Image(
                            painter = painterResource(R.mipmap.img_magnetic_field),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(20.dp)
                        )
                    }
                }

                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(color = White, shape = RoundedCornerShape(10.dp))
                        .clickable {
                            val cameraPermission = arrayOf(Manifest.permission.CAMERA)
                            if (AppPermissionHelper.hasPermissions(context, cameraPermission)) {
                                CameraScannerActivity.launch(context)
                            } else if (activity != null) {
                                pendingAction = FeatureAction.CAMERA
                                AppPermissionHelper.requestPermissionsOrOpenSettings(
                                    activity,
                                    cameraPermission,
                                    cameraPermissionLauncher
                                )
                            }
                        }) {
                        Column(modifier = Modifier.padding(top = 20.dp, start = 15.dp)) {
                            Text("Infrared Camera", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text("Flashing light", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                        }
                        Image(
                            painter = painterResource(R.mipmap.img_infrared_camera),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(20.dp)
                        )
                    }
                }

                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .background(color = White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 15.dp)
                        .clickable {
                            // todo UI图未提供
                        }
                    ) {
                        Text("How it works", color = Color(0xFF152946), fontSize = 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.align(Alignment.CenterStart))
                        Image(
                            painter = painterResource(R.drawable.svg_question),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterEnd).size(24.dp)
                        )
                    }
                }

                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .background(color = White, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 15.dp)
                        .clickable {
                            if (isSubscribed) {
                                HistoryRecordActivity.launch(context)
                            } else {
                                SubscribeActivity.launch(context)
                            }
                        }
                    ) {
                        Text("Detection history", color = Color(0xFF152946), fontSize = 12.sp, fontWeight = FontWeight.Normal, modifier = Modifier.align(Alignment.CenterStart))
                        Image(
                            painter = painterResource(R.drawable.svg_detection_history),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterEnd).size(24.dp)
                        )
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Box(modifier = Modifier.fillMaxWidth().clickable { SubscribeActivity.launch(context) }) {
                        Image(
                            painter = painterResource(R.mipmap.img_home_sub_bg),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Column(modifier = Modifier.padding(start = 15.dp, top = 15.dp)) {
                            Text("41%OFF, Get VIP", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("2.33 per week only", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                            Spacer(modifier = Modifier.height(15.dp))
                            Row(
                                modifier = Modifier
                                    .height(26.dp)
                                    .background(color = Color(0xFF5672FF), shape = RoundedCornerShape(200.dp))
                                    .padding(start = 12.dp, end = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("START", color = White, fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(painter = painterResource(R.drawable.svg_next_with_bg), contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}
