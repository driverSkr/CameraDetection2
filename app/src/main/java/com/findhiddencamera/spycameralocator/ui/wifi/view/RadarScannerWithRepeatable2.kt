package com.findhiddencamera.spycameralocator.ui.wifi.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.findhiddencamera.spycameralocator.R

@Composable
fun RadarScannerWithControls2(isAnimating: MutableState<Boolean>) {

    // 使用独立的动画状态
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by if (isAnimating.value) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    } else {
        // 停止动画时，返回固定值
        remember { mutableFloatStateOf(0f) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.mipmap.img_radar_bg_2),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            contentDescription = "雷达背景"
        )

        if (isAnimating.value) {
            Image(
                painter = painterResource(R.mipmap.img_radar_detect_2),
                contentDescription = "扫描指针",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().padding(61.dp).rotate(rotationAngle)
            )
        }
    }
}