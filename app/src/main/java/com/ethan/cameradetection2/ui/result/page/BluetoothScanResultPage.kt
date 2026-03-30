package com.ethan.cameradetection2.ui.result.page

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.model.BluetoothDevice
import com.ethan.cameradetection2.theme.Green
import com.ethan.cameradetection2.theme.Purple40
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.ui.result.view.BluetoothInfoDevice
import com.ethan.cameradetection2.ui.subscribe.SubscribeActivity
import com.ethan.cameradetection2.ui.wifi.WiFiCamerasActivity
import com.ethan.cameradetection2.utils.findBaseActivityVBind
import com.ethan.cameradetection2.utils.timestampToDate

@Composable
fun BluetoothScanResultPage(suspiciousDevices: List<BluetoothDevice>?, trustedDevices: List<BluetoothDevice>?) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(color = White)) {
        Image(painter = painterResource(R.mipmap.img_history_record_bg), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
                Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                    context.findBaseActivityVBind()?.finish()
                })
                Text("Bluetooth Cameras", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                Image(painter = painterResource(R.drawable.svg_retry_with_bg), contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd).clickable{
                    WiFiCamerasActivity.launch(context)
                })
            }

            LazyColumn(
                contentPadding = PaddingValues(top = 30.dp, start = 15.dp, end = 15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.Bottom) {
                            Text("${suspiciousDevices?.size ?: 0}", color = if ((suspiciousDevices?.size ?: 0) > 0) Color(0xFFF53863) else Green, fontSize = 50.sp, fontWeight = FontWeight.W600, lineHeight = 50.sp)
                            Text("/${(suspiciousDevices?.size ?: 0) + (trustedDevices?.size ?: 0)}", color = if ((suspiciousDevices?.size ?: 0) > 0) Color(0xFFF53863) else Green, fontSize = 30.sp, fontWeight = FontWeight.W600, lineHeight = 50.sp)
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text("Suspected cameras found", color = if ((suspiciousDevices?.size ?: 0) > 0) Color(0xFFF53863) else Green, fontSize = 16.sp, fontWeight = FontWeight.W500)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Bluetooth Online:${timestampToDate()}", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.W400)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Column {
                        Text("Cameras", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.W500)
                        Text("Click any item to learn more details", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400)
                    }
                }

                suspiciousDevices?.let {
                    items(it.size) { index ->
                        BluetoothInfoDevice(it[index])
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("Devices transmiting traffic via Bluetooth", color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.W500)
                            Text("Click any item to learn more details", color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400)
                        }
                    }
                }

                trustedDevices?.let {
                    items(it.size) { index ->
                        BluetoothInfoDevice(it[index])
                    }
                }
            }
        }
    }
}