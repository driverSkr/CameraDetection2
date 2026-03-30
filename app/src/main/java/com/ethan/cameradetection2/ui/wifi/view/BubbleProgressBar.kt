package com.ethan.cameradetection2.ui.wifi.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * 带气泡头部和数值显示的进度条加载动画
 * @param progress 当前进度值，范围0f..1f，支持外部控制或动画驱动
 * @param modifier Modifier修饰符
 * @param barColor 进度条背景颜色
 * @param progressColor 进度条已加载颜色
 * @param bubbleColor 气泡背景颜色
 * @param textColor 气泡内文字颜色
 * @param animate 是否启用动画效果，true时进度变化会平滑过渡
 */
@Composable
fun BubbleProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFFE0E0E0),
    progressColor: Color = Color(0xFF4CAF50),
    bubbleColor: Color = Color(0xFF4CAF50),
    textColor: Color = Color.White,
    animate: Boolean = true
) {
    // 使用 animateFloatAsState 来创建动画值
    val animatedProgress by animateFloatAsState(
        targetValue = if (animate) progress.coerceIn(0f, 1f) else progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    val currentProgress = if (animate) animatedProgress else progress.coerceIn(0f, 1f)

    // 用于测量文本宽度以调整气泡位置
    val textMeasurer = rememberTextMeasurer()
    val progressPercent = (currentProgress * 100).roundToInt()
    val displayText = "${progressPercent}%"
    val textLayoutResult = textMeasurer.measure(
        text = displayText,
        style = TextStyle(
            fontSize = 12.sp,
            color = textColor
        )
    )
    val textWidth = textLayoutResult.size.width.toFloat()
    val textHeight = textLayoutResult.size.height.toFloat()

    // 气泡尺寸：内边距+文本宽高
    val bubbleHorizontalPadding = 8.dp
    val bubbleVerticalPadding = 4.dp
    val bubbleWidth = textWidth + bubbleHorizontalPadding.value * 2
    val bubbleHeight = textHeight + bubbleVerticalPadding.value * 2

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp) // 整体高度，包含气泡和进度条
    ) {
        // 进度条背景（灰色条）
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.CenterStart)
        ) {
            drawRoundRect(
                color = barColor,
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }

        // 进度条前景（彩色条）
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.CenterStart)
        ) {
            val progressWidth = size.width * currentProgress
            if (progressWidth > 0) {
                drawRoundRect(
                    color = progressColor,
                    topLeft = Offset(0f, 0f),
                    size = Size(progressWidth, size.height),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }

        // 气泡头部：圆角矩形气泡，包含进度数值，位置随进度变化
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.CenterStart)
        ) {
            // 计算气泡的X位置：基于进度条进度，但限制边界防止气泡超出左右边界
            val progressBarWidth = size.width
            // 气泡半宽（用于边界限制）
            val bubbleHalfWidth = bubbleWidth.dp.toPx() / 2f
            // 理想中心点X坐标（进度条的进度位置）
            val idealCenterX = progressBarWidth * currentProgress
            // 边界限制：确保气泡完全在Canvas内部（左边距至少为半宽，右边距至少为半宽）
            val clampedCenterX = idealCenterX.coerceIn(bubbleHalfWidth, progressBarWidth - bubbleHalfWidth)
            val bubbleLeft = clampedCenterX - bubbleHalfWidth
            val bubbleTop = 0f // 气泡顶部对齐Canvas顶部（整体高度40dp，气泡高度较小，会显示在上方）
            val bubbleRect = Rect(
                left = bubbleLeft,
                top = bubbleTop,
                right = bubbleLeft + bubbleWidth.dp.toPx(),
                bottom = bubbleTop + bubbleHeight.dp.toPx()
            )

            // 绘制气泡背景（圆角矩形）
            drawRoundRect(
                color = bubbleColor,
                topLeft = Offset(bubbleRect.left, bubbleRect.top),
                size = Size(bubbleRect.width, bubbleRect.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )

            // 绘制文本 - 使用 drawText 方法
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = bubbleRect.left + (bubbleRect.width - textLayoutResult.size.width) / 2f,
                    y = bubbleRect.top + (bubbleRect.height - textLayoutResult.size.height) / 2f
                )
            )
        }
    }
}

/**
 * 示例：带有动画循环演示的完整界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleProgressBarDemo() {
    var targetProgress by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(true) }

    // 自动循环动画效果（模拟加载）
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            while (true) {
                targetProgress = 0f
                kotlinx.coroutines.delay(1000)
                targetProgress
                targetProgress = 1f
                kotlinx.coroutines.delay(2000)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "带气泡头部的进度条动画",
            style = MaterialTheme.typography.headlineSmall
        )

        // 演示气泡进度条
        BubbleProgressBar(
            progress = targetProgress,
            modifier = Modifier.fillMaxWidth(),
            barColor = Color(0xFFDDDDDD),
            progressColor = Color(0xFF2196F3),
            bubbleColor = Color(0xFF2196F3),
            textColor = Color.White,
            animate = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 控制按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = { targetProgress = 0f }) {
                Text("重置 0%")
            }
            Button(onClick = { targetProgress = 0.3f }) {
                Text("30%")
            }
            Button(onClick = { targetProgress = 0.6f }) {
                Text("60%")
            }
            Button(onClick = { targetProgress = 1f }) {
                Text("完成 100%")
            }
            Button(onClick = { isAnimating = !isAnimating }) {
                Text(if (isAnimating) "停止循环" else "开始循环")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("特性说明：", style = MaterialTheme.typography.titleMedium)
                Text("• 圆角矩形气泡位于进度条头部")
                Text("• 气泡内实时显示当前进度百分比")
                Text("• 进度条平滑动画过渡（可配置）")
                Text("• 气泡位置自动限制边界，防止超出屏幕")
                Text("• 支持自定义颜色、尺寸和动画开关")
            }
        }
    }
}

// 为了完整展示，提供一个预览函数（在支持预览的环境中可用）
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewBubbleProgressBar() {
    MaterialTheme {
        BubbleProgressBarDemo()
    }
}