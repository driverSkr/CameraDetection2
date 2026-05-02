package com.findhiddencamera.spycameralocator.ui.history.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.DetectBluetoothDevice
import com.findhiddencamera.spycameralocator.model.DetectWifiDevice
import com.findhiddencamera.spycameralocator.room.DetectDataBase
import com.findhiddencamera.spycameralocator.ui.history.view.BluetoothRecordItemView
import com.findhiddencamera.spycameralocator.ui.history.view.WifiRecordItemView
import com.findhiddencamera.spycameralocator.ui.result.BluetoothScanResultActivity
import com.findhiddencamera.spycameralocator.ui.result.WifiDetectResultActivity
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind

@Composable
fun HistoryRecordPage() {
    val context = LocalContext.current
    val wifiDetectHistory = remember { mutableStateOf<List<DetectWifiDevice>?>(null) }
    val bluetoothDetectHistory = remember { mutableStateOf<List<DetectBluetoothDevice>?>(null) }

    // 合并并排序后的历史记录
    val sortedHistory = remember(wifiDetectHistory.value, bluetoothDetectHistory.value) {
        val allRecords = mutableListOf<HistoryRecord>()

        wifiDetectHistory.value?.forEach { wifiDevice ->
            allRecords.add(HistoryRecord.Wifi(wifiDevice))
        }

        bluetoothDetectHistory.value?.forEach { bluetoothDevice ->
            allRecords.add(HistoryRecord.Bluetooth(bluetoothDevice))
        }

        allRecords.sortedByDescending { it.createTime }
    }

    LaunchedEffect(Unit) {
        wifiDetectHistory.value = DetectDataBase.invoke(context).getWifiDao().getAllDevice()
        bluetoothDetectHistory.value = DetectDataBase.invoke(context).getBluetoothDao().getAllDevice()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.mipmap.img_history_record_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
                Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                    context.findBaseActivityVBind()?.finish()
                })
                Text("Record", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }

            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${sortedHistory.size}", color = Color(0xFF5874FF), fontSize = 50.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cumulative Detections", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sortedHistory.size) { index ->
                    when (val record = sortedHistory[index]) {
                        is HistoryRecord.Wifi -> {
                            WifiRecordItemView(record.device) {
                                WifiDetectResultActivity.launch(
                                    context,
                                    record.device.suspiciousDevices,
                                    record.device.trustedDevices,
                                    record.device.createTime
                                )
                            }
                        }
                        is HistoryRecord.Bluetooth -> {
                            BluetoothRecordItemView(record.device) {
                                BluetoothScanResultActivity.launch(
                                    context,
                                    record.device.suspiciousDevices,
                                    record.device.trustedDevices,
                                    record.device.createTime
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 密封类来表示不同类型的记录
sealed class HistoryRecord {
    abstract val createTime: Long

    data class Wifi(val device: DetectWifiDevice) : HistoryRecord() {
        override val createTime: Long get() = device.createTime
    }

    data class Bluetooth(val device: DetectBluetoothDevice) : HistoryRecord() {
        override val createTime: Long get() = device.createTime
    }
}
