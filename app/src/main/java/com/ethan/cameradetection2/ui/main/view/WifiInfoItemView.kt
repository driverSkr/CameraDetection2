package com.ethan.cameradetection2.ui.main.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.ethan.cameradetection2.model.WifiDevice
import com.ethan.cameradetection2.theme.White
import com.ethan.cameradetection2.theme.White10
import com.ethan.cameradetection2.theme.White60

@Composable
fun WifiInfoItemView(info: WifiDevice, onClick: () -> Unit) {
//    val deviceType = when(info.riskLevel) {
//        1 -> R.drawable.svg_icon_wifi_info_router
//        else -> R.drawable.svg_icon_wifi_info_router
//    }
    val deviceType = when(info.type) {
        "Camera" -> R.drawable.svg_camera
        else -> R.drawable.svg_camera
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(width = 1.dp, color = Color(0x145874FF), shape = RoundedCornerShape(10.dp))
            .background(color = White, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp)
            .clickable{ onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).background(color = Color(0x14939DAA), shape = RoundedCornerShape(8.dp))) {
            Image(painter = painterResource(deviceType), contentDescription = null)
            Image(painter = painterResource(R.drawable.svg_red_light), contentDescription = null, modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp).size(18.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(info.name, color = Color(0xFF152946), fontSize = 14.sp, lineHeight = 14.sp, fontWeight = FontWeight.W500)
            Spacer(modifier = Modifier.height(5.dp))
            Text(info.ip, color = Color(0xFF939DAA), fontSize = 12.sp, lineHeight = 12.sp, fontWeight = FontWeight.W400)
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) {
                Box(modifier = Modifier.size(6.dp).background(color = Color(0xFFF53863), shape = RoundedCornerShape(1.dp)))
            }
        }
    }
}