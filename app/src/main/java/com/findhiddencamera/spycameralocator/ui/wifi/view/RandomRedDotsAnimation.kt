package com.findhiddencamera.spycameralocator.ui.wifi.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.findhiddencamera.spycameralocator.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun RandomRedDotsWithVisibility(
    modifier: Modifier = Modifier,
    maxDots: Int = 5,
    isAnimating: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // 记录当前显示中的红点，扫描停止时会统一清空。
    val visibleDots = remember { mutableStateListOf<DotInfo>() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val areaWidth = maxWidth
        val areaHeight = maxHeight

        LaunchedEffect(isAnimating.value, areaWidth, areaHeight) {
            if (!isAnimating.value) {
                visibleDots.clear()
                return@LaunchedEffect
            }

            while (isAnimating.value) {
                if (visibleDots.size < maxDots) {
                    with(density) {
                        val areaWidthPx = areaWidth.toPx()
                        val areaHeightPx = areaHeight.toPx()
                        val dotSize = 8.dp
                        val dotSizePx = dotSize.toPx()

                        // 雷达是圆形视觉区域，所以红点也限制在圆内随机，避免出现在方形容器四角。
                        val centerX = areaWidthPx / 2f
                        val centerY = areaHeightPx / 2f
                        val radius = min(areaWidthPx, areaHeightPx) / 2f - dotSizePx

                        if (radius > 0f) {
                            val angle = Random.nextDouble(0.0, PI * 2.0)
                            val distance = sqrt(Random.nextDouble()) * radius
                            val xPx = centerX + (cos(angle) * distance).toFloat() - dotSizePx / 2f
                            val yPx = centerY + (sin(angle) * distance).toFloat() - dotSizePx / 2f

                            val dotInfo = DotInfo(
                                id = System.currentTimeMillis() + Random.nextLong(10_000L),
                                x = xPx.toDp(),
                                y = yPx.toDp(),
                                size = dotSize,
                                duration = 1000L + Random.nextLong(3000L)
                            )

                            visibleDots.add(dotInfo)

                            scope.launch {
                                delay(dotInfo.duration)
                                if (isAnimating.value) {
                                    visibleDots.remove(dotInfo)
                                }
                            }
                        }
                    }
                }

                if (!isAnimating.value) break

                delay(500L + Random.nextLong(1500L))
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            visibleDots.forEach { dotInfo ->
                var isVisible by remember(dotInfo.id) { mutableStateOf(true) }

                LaunchedEffect(dotInfo.id, isAnimating.value) {
                    if (!isAnimating.value) {
                        isVisible = false
                        return@LaunchedEffect
                    }

                    delay(dotInfo.duration - 500)
                    if (isAnimating.value) {
                        isVisible = false
                    }
                }

                AnimatedVisibility(
                    visible = isVisible && isAnimating.value,
                    enter = fadeIn(animationSpec = tween(500)) +
                        scaleIn(initialScale = 0.5f, animationSpec = tween(500)),
                    exit = fadeOut(animationSpec = tween(500)) +
                        scaleOut(targetScale = 0.5f, animationSpec = tween(500)),
                    modifier = Modifier
                        .size(dotInfo.size)
                        .offset(dotInfo.x, dotInfo.y)
                ) {
                    Image(
                        painter = painterResource(R.drawable.svg_position),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
    }
}

data class DotInfo(
    val id: Long,
    val x: Dp,
    val y: Dp,
    val size: Dp,
    val duration: Long
)
