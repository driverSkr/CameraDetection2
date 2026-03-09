package com.ethan.cameradetection2.ui.bluetooth.page

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.ui.bluetooth.view.RadarScannerWithControls3
import com.ethan.cameradetection2.utils.findBaseActivityVBind

@Composable
fun BluetoothCamerasPage() {
    val context = LocalContext.current
    val isAnimating = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimating.value = true
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
            Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                context.findBaseActivityVBind()?.finish()
            })
            Text("Bluetooth Cameras", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            RadarScannerWithControls3(isAnimating)
            Row(modifier = Modifier.align(Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically) {
                Text("Found Devices:", color = Color(0xFF152946), fontSize = 16.sp)
                Text("99", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).padding(horizontal = 30.dp).background(color = Color(0xFF5874FF)))

        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Camera Detector Ready...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Detecting Suspicious Devices...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Analyzing Device Ports...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Identifying Camera...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Collate Detection Results...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
        }
    }
}