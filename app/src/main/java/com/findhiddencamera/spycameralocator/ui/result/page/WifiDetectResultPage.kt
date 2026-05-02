package com.findhiddencamera.spycameralocator.ui.result.page

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.ui.result.WifiDetectDetailActivity
import com.findhiddencamera.spycameralocator.ui.result.view.WifiInfoItemView
import com.findhiddencamera.spycameralocator.ui.subscribe.SubscribeActivity
import com.findhiddencamera.spycameralocator.ui.wifi.WiFiCamerasActivity
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WifiDetectResultPage(
    suspiciousDevices: List<WifiDevice>?,
    trustedDevices: List<WifiDevice>?,
    scanTimeSeconds: Long = System.currentTimeMillis().div(1000)
) {
    val context = LocalContext.current
    val wifiName = remember { getCurrentWifiName(context) }
    val scanTimeText = remember(scanTimeSeconds) { formatScanTime(scanTimeSeconds) }
    val devices = remember(suspiciousDevices, trustedDevices) {
        buildResultDevices(suspiciousDevices, trustedDevices)
    }
    val cameraDevices = remember(devices) { devices.filter { it.isCameraDevice() } }
    val wifiTrafficDevices = remember(devices) { devices.filterNot { it.isCameraDevice() } }
    val totalCount = devices.size
    val cameraCount = cameraDevices.size
    val hasCamera = cameraCount > 0
    val summaryColor = Color(0xFFF53863)
    val scanInfoText = remember(wifiName, scanTimeText) {
        "WiFi Name:$wifiName $scanTimeText"
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
                    "WiFi Cameras",
                    color = Color(0xFF152946),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
                Image(
                    painter = painterResource(R.drawable.svg_retry_with_bg),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.CenterEnd).clickable {
                        WiFiCamerasActivity.launch(context)
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
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.Bottom
                        ) {
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
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (hasCamera) "Suspected cameras found" else "Supspeted Cameras",
                            color = summaryColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            scanInfoText,
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
                        items(cameraDevices.size) { index ->
                            val device = cameraDevices[index]
                            WifiInfoItemView(device) {
                                WifiDetectDetailActivity.launch(context, device)
                            }
                        }
                    }

                    item {
                        ResultSectionHeader(
                            title = "Devices transmitting traffic via WiFi",
                            showUnlock = true,
                            onUnlockClick = { SubscribeActivity.launch(context) }
                        )
                    }
                    items(wifiTrafficDevices.size) { index ->
                        val device = wifiTrafficDevices[index]
                        WifiInfoItemView(device) {
                            WifiDetectDetailActivity.launch(context, device)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultSectionHeader(
    title: String,
    showUnlock: Boolean = false,
    onUnlockClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
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

        if (showUnlock) {
            Row(
                modifier = Modifier
                    .height(24.dp)
                    .background(Color(0xFFE8BF78), RoundedCornerShape(8.dp))
                    .clickable { onUnlockClick.invoke() }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.svg_unlock_chip_lock),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    "Unlock",
                    color = Color(0xFF8B5D21),
                    fontSize = 11.sp,
                    lineHeight = 11.sp,
                    fontWeight = FontWeight.W500
                )
            }
        }
    }
}

private fun buildResultDevices(
    suspiciousDevices: List<WifiDevice>?,
    trustedDevices: List<WifiDevice>?
): List<WifiDevice> {
    return (suspiciousDevices.orEmpty() + trustedDevices.orEmpty())
        .distinctBy { it.ip.ifBlank { it.mac.ifBlank { it.name } } }
        .sortedWith(
            compareByDescending<WifiDevice> { it.isCameraDevice() }
                .thenBy { it.riskSortOrder() }
                .thenBy { it.ip }
        )
}

private fun WifiDevice.isCameraDevice(): Boolean {
    return type.equals("Camera", true)
}

private fun WifiDevice.riskSortOrder(): Int {
    return when {
        riskLevel == 0 && !isCameraDevice() -> 3
        isCameraDevice() -> 0
        ping in 0..100 -> 0
        ping in 101..200 -> 1
        ping > 200 -> 2
        else -> 3
    }
}

private fun formatScanTime(scanTimeSeconds: Long): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        .format(Date(scanTimeSeconds * 1000L))
}

@SuppressLint("MissingPermission")
private fun getCurrentWifiName(context: Context): String {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    val ssid = wifiManager?.connectionInfo?.ssid
        ?.trim()
        ?.removePrefix("\"")
        ?.removeSuffix("\"")

    return if (ssid.isNullOrBlank() || ssid.equals("<unknown ssid>", true)) {
        "Unknown WiFi"
    } else {
        ssid
    }
}
