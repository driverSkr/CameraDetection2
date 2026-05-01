package com.findhiddencamera.spycameralocator.ui.wifi.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.findhiddencamera.spycameralocator.R

private const val RadarSweepScale = 744f / 1080f

@Composable
fun RadarScannerWithControls2(
    isAnimating: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    // 使用独立的旋转动画，停止扫描时角度回到初始位置。
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
        remember { mutableFloatStateOf(0f) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Image(
            painter = painterResource(R.mipmap.img_radar_bg_2),
            contentDescription = "雷达背景",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        if (isAnimating.value) {
            // 扫描光束按素材比例缩放并居中，雷达区域宽度变化时仍然保持圆心对齐。
            Image(
                painter = painterResource(R.mipmap.img_radar_detect_2),
                contentDescription = "扫描光束",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize(RadarSweepScale)
                    .align(Alignment.Center)
                    .rotate(rotationAngle)
            )
        }
    }
}
