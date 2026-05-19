package com.findhiddencamera.spycameralocator.ui.result.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.theme.White

@Composable
fun WifiInfoItemView(modifier: Modifier, info: WifiDevice, onClick: () -> Unit) {
    val risk = riskUi(info)
    val typeLabel = info.displayType()

    Row(
        modifier = modifier
            .fillMaxSize()
            .shadow(elevation = 7.dp, shape = RoundedCornerShape(8.dp), clip = false)
            .border(width = 1.dp, color = Color(0x0D5874FF), shape = RoundedCornerShape(8.dp))
            .background(color = White, shape = RoundedCornerShape(8.dp))
            .clickable { onClick.invoke() }
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeviceTypeIcon(type = typeLabel, risk = risk)

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = info.displayName(),
                color = Color(0xFF152946),
                fontSize = 14.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.W600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = info.ip,
                color = Color(0xFF939DAA),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.W400,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        SignalBlocks(activeBlocks = risk.signalBlocks, color = risk.color)
    }
}

@Composable
private fun DeviceTypeIcon(type: String, risk: WifiRiskUi) {
    val iconRes = when (type.lowercase()) {
        "camera" -> R.drawable.svg_camera
        "phone" -> R.drawable.svg_icon_sensor
        "pc", "computer", "windows pc" -> R.drawable.svg_pc
        "router", "tv", "apple" -> R.drawable.svg_icon_wifi_info_router
        else -> R.drawable.svg_icon_sensor
    }
    val useImageWithoutTint = type.equals("Camera", true)

    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Color(0xFFF7F8FA), RoundedCornerShape(7.dp))
    ) {
        if (useImageWithoutTint) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center).size(32.dp)
            )
        } else {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color(0xFF152946),
                modifier = Modifier.align(Alignment.Center).size(20.dp)
            )
        }
        RiskLamp(
            risk = risk,
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 5.dp, y = (-5).dp)
        )
    }
}

@Composable
private fun RiskLamp(risk: WifiRiskUi, modifier: Modifier = Modifier) {
    if (risk.signalBlocks == 3) {
        Image(
            painter = painterResource(R.drawable.svg_red_light),
            contentDescription = null,
            modifier = modifier.size(14.dp)
        )
    } else {
        Box(
            modifier = modifier
                .size(8.dp)
                .background(risk.color, RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun SignalBlocks(activeBlocks: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(5.dp)
                    .background(
                        color = if (index < activeBlocks) color else Color(0xFFE9EDF2),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
            if (index != 2) {
                Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}

private data class WifiRiskUi(
    val color: Color,
    val signalBlocks: Int
)

private fun riskUi(info: WifiDevice): WifiRiskUi {
    return when {
        info.type.equals("Camera", true) ->
            WifiRiskUi(Color(0xFFF53863), 3)
        info.riskLevel == 0 ->
            WifiRiskUi(Color(0xFF05CA67), 1)
        info.ping in 0..100 ->
            WifiRiskUi(Color(0xFFF53863), 3)
        info.ping in 101..200 ->
            WifiRiskUi(Color(0xFFFFC107), 2)
        else ->
            WifiRiskUi(Color(0xFF05CA67), 1)
    }
}

private fun WifiDevice.displayName(): String {
    val rawName = name.trim()
    return if (
        rawName.isBlank() ||
        rawName.equals("Unknown", true) ||
        rawName.equals("Unknown Device", true) ||
        rawName.equals("Device", true)
    ) {
        "Suspected Devices"
    } else {
        rawName
    }
}

private fun WifiDevice.displayType(): String {
    val rawType = type.trim()
    return if (rawType.isBlank() || rawType.equals("Device", true)) {
        "Unknown"
    } else {
        rawType
    }
}
