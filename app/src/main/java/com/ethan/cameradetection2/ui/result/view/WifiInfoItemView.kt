package com.ethan.cameradetection2.ui.result.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ethan.cameradetection2.R
import com.ethan.cameradetection2.model.WifiDevice
import com.ethan.cameradetection2.theme.White

@Composable
fun WifiInfoItemView(info: WifiDevice) {
    val deviceType = when(info.riskLevel) {
        1 -> R.drawable.svg_icon_wifi_info_router
        else -> R.drawable.svg_icon_wifi_info_router
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(71.dp)
            .border(width = 1.dp, color = Color(0x145874FF), shape = RoundedCornerShape(20.dp))
            .background(color = White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp)
            .clickable{  },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(deviceType), contentDescription = null)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(info.name, color = Color(0xFF152946), fontSize = 14.sp, fontWeight = FontWeight.W500)
            Spacer(modifier = Modifier.height(3.dp))
            Text(info.ip, color = Color(0xFF939DAA), fontSize = 12.sp, fontWeight = FontWeight.W400)
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(painter = painterResource(if (info.riskLevel == 0) R.drawable.svg_icon_safety else R.drawable.svg_icon_risk), contentDescription = null)
    }
}