package com.findhiddencamera.spycameralocator.ui.wifi.page

import android.content.Context
import android.net.wifi.WifiManager
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.DetectWifiDevice
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.room.DetectDataBase
import com.findhiddencamera.spycameralocator.ui.result.WifiDetectResultActivity
import com.findhiddencamera.spycameralocator.ui.wifi.view.RadarScannerWithControls2
import com.findhiddencamera.spycameralocator.ui.wifi.view.RandomRedDotsWithVisibility
import com.findhiddencamera.spycameralocator.utils.WifiHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.subnet.Device

@Composable
fun WiFiCamerasPage() {
    val context = LocalContext.current
    val isAnimating = remember { mutableStateOf(true) }
    val suspiciousDevices = remember { mutableStateListOf<WifiDevice>() }
    val trustedDevices = remember { mutableStateListOf<WifiDevice>() }
    val detectProgress = remember { mutableIntStateOf(0) }
    val localIp = remember {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        String.format(
            "%d.%d.%d.%d",
            wifiInfo.ipAddress and 0xff,
            wifiInfo.ipAddress shr 8 and 0xff,
            wifiInfo.ipAddress shr 16 and 0xff,
            wifiInfo.ipAddress shr 24 and 0xff
        )
    }
    val loadingState = remember { MutableLiveData(0) }
    val currentLoadingState by loadingState.observeAsState(0)

    LaunchedEffect(Unit) {
        wifiDetect(localIp, suspiciousDevices, trustedDevices, isAnimating, detectProgress, loadingState)
    }

    LaunchedEffect(isAnimating.value) {
        if (!isAnimating.value && detectProgress.intValue == 100) {
            val suspiciousDevicesList = ArrayList(suspiciousDevices.toList())
            val trustedDevicesList = ArrayList(trustedDevices.toList())
            DetectDataBase.invoke(context).getWifiDao().addDevice(DetectWifiDevice(suspiciousDevices = suspiciousDevices, trustedDevices = trustedDevices))
            WifiDetectResultActivity.launch(context, suspiciousDevicesList, trustedDevicesList)
            context.findBaseActivityVBind()?.finish()
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
            Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                context.findBaseActivityVBind()?.finish()
            })
            Text("WiFi Cameras", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            RadarScannerWithControls2(isAnimating)
            RandomRedDotsWithVisibility(modifier = Modifier.align(Alignment.Center), isAnimating = isAnimating)
            Row(modifier = Modifier.align(Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically) {
                Text("Found Devices:", color = Color(0xFF152946), fontSize = 16.sp)
                Text("${suspiciousDevices.size + trustedDevices.size}", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).padding(horizontal = 30.dp).background(color = Color(0xFF5874FF)))

        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Camera Detector Ready...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 1) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Detecting Suspicious Devices...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 2) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Analyzing Device Ports...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 3) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Identifying Camera...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 4) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Collate Detection Results...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 5) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
    }
}

private fun wifiDetect(
    localIp: String,
    suspiciousDevices: SnapshotStateList<WifiDevice>,
    trustedDevices: SnapshotStateList<WifiDevice>,
    isAnimating: MutableState<Boolean>,
    detectProgress: MutableIntState,
    loadingState: MutableLiveData<Int>
) {
    loadingState.postValue(1)
    SubnetDevices.fromLocalAddress().findDevices(object : SubnetDevices.OnSubnetDeviceFound {
        override fun onDeviceFound(device: Device?) {
            if (detectProgress.intValue < 100) {
                if (loadingState.value == 1) {
                    loadingState.postValue(2)
                }
                detectProgress.intValue += 1
            }
        }

        override fun onFinished(devicesFound: ArrayList<Device?>?) {
            if (devicesFound == null) {
                loadingState.postValue(5)
                return
            }
            // 并发检测每个IP的类型
            val threads = mutableListOf<Thread>()
            for (dev in devicesFound) {
                val t = Thread {
                    loadingState.postValue(3)
                    val wifiDevice = WifiHelper.detectDeviceType(dev!!, localIp)
                    if (wifiDevice.riskLevel > 0) {
                        suspiciousDevices.add(wifiDevice)
                    } else {
                        trustedDevices.add(wifiDevice)
                    }
                }
                threads.add(t)
                t.start()
                if (threads.size >= 10) {
                    threads.removeAll { !it.isAlive }
                }
            }
            loadingState.postValue(4)
            threads.forEach { it.join(3000) }

            loadingState.postValue(5)
            isAnimating.value = false
            detectProgress.intValue = 100
        }
    })
}