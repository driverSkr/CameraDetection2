package com.findhiddencamera.spycameralocator.ui.result.page

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ethan.pay.utils.SubHelper
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.dialog.DialogHelper
import com.findhiddencamera.spycameralocator.dialog.rememberLoadingDialog
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.model.SubModel
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.ui.camera.CameraScannerActivity
import com.findhiddencamera.spycameralocator.ui.magnetic.MagneticFieldActivity
import com.findhiddencamera.spycameralocator.ui.result.view.BluetoothRiskLampView
import com.findhiddencamera.spycameralocator.ui.subscribe.viewmodel.SubscribeViewModel
import com.findhiddencamera.spycameralocator.ui.wifi.WiFiCamerasActivity
import com.findhiddencamera.spycameralocator.utils.AppPermissionHelper
import com.findhiddencamera.spycameralocator.utils.DetectionSessionHelper
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.WifiHelper
import com.findhiddencamera.spycameralocator.utils.findActivity
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun BluetoothScanDetailPage(device: BluetoothDevice?) {
    val context = LocalContext.current
    val isSubscribed by SubscribeHelper.isSubscribedFlow.collectAsState()
    val lockedCountHazeState = remember { HazeState() }
    val dialog = rememberLoadingDialog()
    val subscribeViewModel = context.findBaseActivityVBind()?.let { viewModel<SubscribeViewModel>(it) }
    var monthlyProduct by remember { mutableStateOf<SubModel?>(null) }
    var isGuideDialogShowing by remember { mutableStateOf(false) }
    val lockedHazeStyle = remember {
        HazeStyle(backgroundColor = White, tint = null, blurRadius = 12.dp)
    }

    /**
     * 未订阅详情页点击模糊区域时直接购买月套餐。
     */
    fun buyMonthlyProduct() {
        val activity = context.findActivity() as? FragmentActivity
        if (monthlyProduct != null && activity != null) {
            dialog.value = true
            subscribeViewModel?.buySubscribe(monthlyProduct, activity, dialog)
        } else {
            dialog.value = false
            Toast.makeText(context, "no product", Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var checkWifiPermissionAndLaunch: () -> Unit
    lateinit var openWifiSettingsForScan: () -> Unit

    fun launchWifiDetectAndClose() {
        val activity = context.findActivity() as? FragmentActivity
        if (activity != null) {
            WiFiCamerasActivity.launch(activity)
            activity.finish()
        } else {
            context.findBaseActivityVBind()?.finish()
        }
    }

    val wifiPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val activity = context.findActivity() as? FragmentActivity
        if (WifiHelper.hasWifiPermission(context)) {
            launchWifiDetectAndClose()
        } else if (activity != null && AppPermissionHelper.shouldOpenSettings(activity, WifiHelper.requiredPermissions())) {
            AppPermissionHelper.openAppPermissionSettings(activity)
            activity.finish()
        } else {
            context.findBaseActivityVBind()?.finish()
        }
    }

    val wifiEnableLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (WifiHelper.isWifiEnabled(context)) {
            checkWifiPermissionAndLaunch()
        } else {
            context.findBaseActivityVBind()?.finish()
        }
    }

    checkWifiPermissionAndLaunch = {
        val activity = context.findActivity() as? FragmentActivity
        if (WifiHelper.hasWifiPermission(context)) {
            launchWifiDetectAndClose()
        } else if (activity != null) {
            AppPermissionHelper.requestPermissionsOrOpenSettings(
                activity,
                WifiHelper.requiredPermissions(),
                wifiPermissionLauncher
            )
        } else {
            context.findBaseActivityVBind()?.finish()
        }
    }

    openWifiSettingsForScan = {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_WIFI)
        } else {
            Intent(Settings.ACTION_WIFI_SETTINGS)
        }
        wifiEnableLauncher.launch(intent)
    }

    fun startWifiDetectFromGuide() {
        val activity = context.findActivity() as? FragmentActivity
        if (activity == null) {
            context.findBaseActivityVBind()?.finish()
            return
        }
        if (!WifiHelper.isWifiEnabled(context)) {
            DialogHelper.requestWifiPermissionDialog(
                activity = activity,
                onAllow = {
                    openWifiSettingsForScan()
                },
                onCancel = {
                    activity.finish()
                }
            )
            return
        }
        checkWifiPermissionAndLaunch()
    }

    fun exitDetailPage() {
        val activity = context.findActivity() as? FragmentActivity
        val baseActivity = context.findBaseActivityVBind()
        if (!isSubscribed && !DetectionSessionHelper.hasWifiDetected && activity != null && !isGuideDialogShowing) {
            isGuideDialogShowing = true
            DialogHelper.guideCheckDialog(
                activity = activity,
                content = "Hide & Spy Cameras stream your video to nearby devices via Wifi, so be sure to check as soon as possible.",
                imageRes = R.mipmap.img_wifi_radar_plate,
                onStart = {
                    startWifiDetectFromGuide()
                },
                onCancel = {
                    baseActivity?.finish()
                }
            )
        } else {
            baseActivity?.finish()
        }
    }

    BackHandler {
        exitDetailPage()
    }

    LaunchedEffect(Unit) {
        val queryResult = subscribeViewModel?.querySubProduct(context)
        monthlyProduct = queryResult
            ?.firstOrNull { it.id == SubHelper.getMonthPlanId() }
            ?: queryResult?.firstOrNull()
    }

    Box(modifier = Modifier.fillMaxSize().background(color = White)) {
        Image(
            painter = painterResource(R.mipmap.img_history_record_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(
                    modifier = if (isSubscribed) {
                        Modifier.statusBarsPadding().padding(horizontal = 15.dp).fillMaxSize()
                    } else {
                        // 保持与 WiFi 详情页一致的整块上半区 haze 尺寸。
                        Modifier.statusBarsPadding().padding(horizontal = 15.dp).fillMaxSize().haze(lockedCountHazeState)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isSubscribed) {
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 9.dp)) {
                            Image(
                                painter = painterResource(R.drawable.svg_back),
                                contentDescription = null,
                                modifier = Modifier.align(Alignment.CenterStart).clickable {
                                    exitDetailPage()
                                }
                            )
                            Text("Details", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                        }
                    } else {
                        // 未订阅时底层 Tab 不参与 haze 采样，避免清晰层下方透出黑色重影。
                        Spacer(modifier = Modifier.fillMaxWidth().padding(top = 9.dp).height(30.dp))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(modifier = Modifier.size(70.dp).background(color = Color(0xFF939DAA).copy(0.08f), shape = RoundedCornerShape(12.dp))) {
                        Image(painter = painterResource(R.drawable.svg_camera), contentDescription = null, modifier = Modifier.align(Alignment.Center))
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(device?.name ?: "Unknown", color = Color(0xFF152946), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(40.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardColors(contentColor = White, containerColor = White, disabledContainerColor = White, disabledContentColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(
                            width = 1.dp,  // 边框宽度
                            color = Color(0xFF5874FF).copy(0.08f)  // 边框颜色
                        )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 21.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(R.drawable.svg_network_state), contentDescription = null)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Online now", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(if (device?.connected == true) "Yes" else "No", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(R.drawable.svg_signal), contentDescription = null)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("RSSI Score", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("${device?.rssi ?: 0}dBm", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(R.drawable.svg_question), contentDescription = null)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Type", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(device?.type ?: "Unknown", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(25.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(R.drawable.svg_pc), contentDescription = null)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("MAC", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(device?.mac ?: "Unknown", color = Color(0xFF939DAA), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (!isSubscribed) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 43.dp)
                            .fillMaxSize()
                            .hazeChild(lockedCountHazeState, style = lockedHazeStyle)
                            .clickable {
                                buyMonthlyProduct()
                            },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(painter = painterResource(R.mipmap.img_lock), contentDescription = null, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(50.dp))
                            Box(modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth().height(60.dp).clip(shape = RoundedCornerShape(10.dp))) {
                                Image(painter = painterResource(R.mipmap.img_btn_bg), contentScale = ContentScale.FillBounds, contentDescription = null, modifier = Modifier.fillMaxSize())
                                Text("Camera Details", color = White, fontSize = 16.sp, fontWeight = FontWeight.W500, modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }

                Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 15.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    // 顶部栏和角标作为清晰层最后绘制，避免被 hazeChild 覆盖。
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 9.dp)) {
                        Image(
                            painter = painterResource(R.drawable.svg_back),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterStart).clickable {
                                exitDetailPage()
                            }
                        )
                        Text("Details", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(modifier = Modifier.size(70.dp)) {
                        device?.let {
                            BluetoothRiskLampView(
                                info = it,
                                modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp).size(24.dp)
                            )
                        }
                    }
                }
            }

            Text("Suspected hidden camera. Locate it immediately.", color = Color(0xFF44546B), fontSize = 14.sp, fontWeight = FontWeight.W700, modifier = Modifier.padding(horizontal = 15.dp).fillMaxWidth())
            Spacer(modifier = Modifier.height(15.dp))
            Box(modifier = Modifier.padding(horizontal = 15.dp).clickable { MagneticFieldActivity.launch(context) }.fillMaxWidth().height(114.dp).background(color = Color(0xFF8095FF), shape = RoundedCornerShape(10.dp))) {
                Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
                    Text("Find out the operating range of equipment by magnetic field signal", color = White, fontSize = 12.sp, fontWeight = FontWeight.W500, lineHeight = 12.sp, modifier = Modifier.fillMaxWidth().padding(end = 125.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.height(36.dp).background(color = White, shape = RoundedCornerShape(8.dp)).padding(horizontal = 14.dp)) {
                        Text("Detect Now", color = Color(0xFF5672FF), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.padding(horizontal = 15.dp).clickable { CameraScannerActivity.launch(context) }.fillMaxWidth().height(114.dp).background(color = Color(0xFF8095FF), shape = RoundedCornerShape(10.dp))) {
                Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
                    Text("Find the flickering pinhole camera through the infrared camera.", color = White, fontSize = 12.sp, fontWeight = FontWeight.W500, lineHeight = 12.sp, modifier = Modifier.fillMaxWidth().padding(end = 125.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.height(36.dp).background(color = White, shape = RoundedCornerShape(8.dp)).padding(horizontal = 14.dp)) {
                        Text("Detect Now", color = Color(0xFF5672FF), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
