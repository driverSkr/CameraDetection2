package com.ethan.cameradetection2.ui.history.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.model.DetectWifiDevice
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.utils.timestampToDate
import com.ethan.cameradetection2.utils.timestampToDateSS

@Composable
fun WifiRecordItemView(detect: DetectWifiDevice, clickable: () -> Unit) {
    Row(modifier = Modifier
        .clickable{ clickable.invoke() }
        .fillMaxWidth()
        .border(width = 1.dp, color = Color(0x145874FF), shape = RoundedCornerShape(10.dp))
        .background(color = White, shape = RoundedCornerShape(10.dp))
        .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.mipmap.img_history_wifi), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text(detect.createTime.toInt().timestampToDateSS(), color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("${detect.suspiciousDevices.size} Suspected Devices", color = Color(0xFF44546B), fontSize = 12.sp)
        }
        Image(painter = painterResource(R.drawable.svg_next), contentDescription = null)
    }
}