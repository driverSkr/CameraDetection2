package com.findhiddencamera.spycameralocator.ui.result.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.ui.bluetooth.BluetoothCamerasActivity
import com.findhiddencamera.spycameralocator.ui.result.BluetoothScanDetailActivity
import com.findhiddencamera.spycameralocator.ui.result.view.BluetoothInfoDevice
import com.findhiddencamera.spycameralocator.ui.result.view.BluetoothRiskLampView
import com.findhiddencamera.spycameralocator.ui.result.view.BluetoothSignalBlocksView
import com.findhiddencamera.spycameralocator.ui.subscribe.SplashScreenSubscribeActivity
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BluetoothScanResultPage(
    suspiciousDevices: List<BluetoothDevice>?,
    trustedDevices: List<BluetoothDevice>?,
    scanTimeSeconds: Long = System.currentTimeMillis().div(1000)
) {
    val lockedCountHazeState = remember { HazeState() }
    val context = LocalContext.current
    val isSubscribed by SubscribeHelper.isSubscribedFlow.collectAsState()
    val scanTimeText = remember(scanTimeSeconds) { formatScanTime(scanTimeSeconds) }
    val devices = remember(suspiciousDevices, trustedDevices) {
        buildResultDevices(suspiciousDevices, trustedDevices)
    }
    val cameraDevices = remember(devices) { devices.filter { it.isCameraDevice() } }
    val bluetoothTrafficDevices = remember(devices) { devices.filterNot { it.isCameraDevice() } }
    val totalCount = devices.size
    val cameraCount = cameraDevices.size
    val hasCamera = cameraCount > 0
    val summaryColor = Color(0xFFF53863)
    val lockedHazeStyle = remember {
        HazeStyle(backgroundColor = White, tint = null, blurRadius = 12.dp)
    }

    Box(modifier = Modifier.fillMaxSize().background(color = White)) {
        Image(
            painter = painterResource(R.mipmap.img_history_record_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp, end = 15.dp)) {
                Image(
                    painter = painterResource(R.drawable.svg_back),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterStart).clickable {
                        context.findBaseActivityVBind()?.finish()
                    }
                )
                Text(
                    "Bluetooth Cameras",
                    color = Color(0xFF152946),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                Image(
                    painter = painterResource(R.drawable.svg_retry_with_bg),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd).clickable {
                        BluetoothCamerasActivity.launch(context)
                    }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(top = 27.dp, start = 26.dp, end = 26.dp, bottom = 37.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.height(64.dp).width(194.dp).align(Alignment.CenterHorizontally)) {
                            Box(
                                modifier = if (isSubscribed) Modifier.fillMaxSize() else Modifier.fillMaxSize().haze(lockedCountHazeState),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = if (hasCamera) "$cameraCount" else "$totalCount",
                                        color = summaryColor,
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 40.sp
                                    )
                                    if (hasCamera) {
                                        Text(
                                            text = "/$totalCount",
                                            color = summaryColor,
                                            fontSize = 23.sp,
                                            fontWeight = FontWeight.W600,
                                            lineHeight = 33.sp
                                        )
                                    }
                                }
                            }

                            if (!isSubscribed) {
                                // 未订阅用户使用真实扫描数据参与高斯模糊，点击后走订阅页关闭再进详情链路。
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(width = 1.dp, color = Color(0xFF5874FF).copy(alpha = 0.08f), shape = RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                        .hazeChild(lockedCountHazeState, style = lockedHazeStyle)
                                        .clickable {
                                            devices.firstOrNull()?.let { openLockedBluetoothDetail(context, it) }
                                                ?: SplashScreenSubscribeActivity.launch(context)
                                        }
                                ) {
                                    Image(painter = painterResource(R.mipmap.img_lock), contentDescription = null, modifier = Modifier.align(Alignment.Center).size(32.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (hasCamera) "Suspected cameras found" else "Supspeted Cameras",
                            color = summaryColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Bluetooth Online:$scanTimeText",
                            color = Color(0xFF152946),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (devices.isEmpty()) {
                    item {
                        Text(
                            "No devices found",
                            color = Color(0xFF939DAA),
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                        )
                    }
                } else {
                    if (cameraDevices.isNotEmpty()) {
                        item {
                            ResultSectionHeader(title = "Cameras")
                        }
                        items(
                            count = cameraDevices.size,
                            key = { index -> cameraDevices[index].stableListKey() }
                        ) { index ->
                            val device = cameraDevices[index]
                            if (isSubscribed) {
                                BluetoothInfoDevice(modifier = Modifier.fillMaxWidth().height(56.dp), info = device) {
                                    BluetoothScanDetailActivity.launch(context, device)
                                }
                            } else {
                                LockedBluetoothInfoDevice(
                                    device = device,
                                    lockedHazeStyle = lockedHazeStyle
                                ) {
                                    openLockedBluetoothDetail(context, device)
                                }
                            }
                        }
                    }

                    item {
                        ResultSectionHeader(title = "Devices transmitting traffic via Bluetooth")
                    }
                    items(
                        count = bluetoothTrafficDevices.size,
                        key = { index -> bluetoothTrafficDevices[index].stableListKey() }
                    ) { index ->
                        val device = bluetoothTrafficDevices[index]
                        if (isSubscribed) {
                            BluetoothInfoDevice(modifier = Modifier.fillMaxWidth().height(56.dp), info = device) {
                                BluetoothScanDetailActivity.launch(context, device)
                            }
                        } else {
                            LockedBluetoothInfoDevice(
                                device = device,
                                lockedHazeStyle = lockedHazeStyle
                            ) {
                                openLockedBluetoothDetail(context, device)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LockedBluetoothInfoDevice(
    device: BluetoothDevice,
    lockedHazeStyle: HazeStyle,
    onClick: () -> Unit
) {
    // 每个锁定卡片单独持有 HazeState，保留独立采样，避免不同 item 之间的模糊缓存互相影响。
    val itemHazeState = remember(device.mac, device.name) { HazeState() }

    Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
        BluetoothInfoDevice(
            modifier = Modifier.haze(itemHazeState),
            info = device,
            showRiskLamp = false,
            showSignalBlocks = false
        ) {
            onClick.invoke()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick.invoke() }
                .hazeChild(itemHazeState, style = lockedHazeStyle)
        )
        // 风险角标和右侧信号格作为清晰层单独绘制，不参与底层 haze 采样。
        BluetoothRiskLampView(
            info = device,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 37.dp, top = 5.dp)
        )
        BluetoothSignalBlocksView(
            info = device,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp).clickable { onClick.invoke() }
        )
    }
}

@Composable
private fun ResultSectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
        Text(
            title,
            color = Color(0xFF152946),
            fontSize = 12.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.W500
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            "Click any item to learn more details",
            color = Color(0xFF939DAA),
            fontSize = 10.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.W400
        )
    }
}

private fun buildResultDevices(
    suspiciousDevices: List<BluetoothDevice>?,
    trustedDevices: List<BluetoothDevice>?
): List<BluetoothDevice> {
    return (suspiciousDevices.orEmpty() + trustedDevices.orEmpty())
        .distinctBy { it.mac.ifBlank { it.name } }
        .sortedWith(
            compareByDescending<BluetoothDevice> { it.isCameraDevice() }
                .thenBy { it.riskSortOrder() }
                .thenBy { it.displayNameForSort() }
        )
}

private fun openLockedBluetoothDetail(context: android.content.Context, device: BluetoothDevice) {
    // 未订阅锁定态先打开开屏订阅页，只有该入口关闭订阅页后才进入对应设备详情。
    SplashScreenSubscribeActivity.launchForDeviceDetailAfterClose(context, device)
}

private fun BluetoothDevice.stableListKey(): String {
    // LazyColumn 使用稳定 key，降低列表复用时 haze 缓存和设备数据错位的概率。
    return mac.ifBlank { name }
}

private fun BluetoothDevice.isCameraDevice(): Boolean {
    return type.equals("Camera", true)
}

private fun BluetoothDevice.riskSortOrder(): Int {
    return when {
        isCameraDevice() -> 0
        rssi > -50 -> 0
        rssi >= -85 -> 1
        else -> 2
    }
}

private fun BluetoothDevice.displayNameForSort(): String {
    return name.ifBlank { "Suspected Devices" }
}

private fun formatScanTime(scanTimeSeconds: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        .format(Date(scanTimeSeconds * 1000L))
}
