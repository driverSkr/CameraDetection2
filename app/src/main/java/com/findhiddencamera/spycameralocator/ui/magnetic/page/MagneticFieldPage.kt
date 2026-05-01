package com.findhiddencamera.spycameralocator.ui.magnetic.page

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.theme.White
import com.findhiddencamera.spycameralocator.theme.White50
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import kotlin.math.sqrt

// 仪表盘刻度从左下角开始，顺时针扫到右下角，总跨度约 270 度。
private const val GAUGE_START_ANGLE = -135f
private const val GAUGE_SWEEP_ANGLE = 270f

// 指针图片的视觉旋转中心在底部圆球中心，而不是图片底部边缘。
// 原图尺寸为 144x297，圆球中心约在 y=224.5px，所以这里按比例设置旋转锚点。
private const val POINTER_PIVOT_X = 0.5f
private const val POINTER_PIVOT_Y = 224.5f / 297f

@Composable
fun MagneticFieldPage() {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val magneticSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) }
    var magneticGauge by remember { mutableIntStateOf(0) }

    val magneticSensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        val x = it.values[0]
                        val y = it.values[1]
                        val z = it.values[2]

                        // 计算三轴磁场强度，并归一化成仪表盘 0-100 的显示值。
                        val magnitude = sqrt(x * x + y * y + z * z)

                        val normalizedValue = when {
                            magnitude < 20f -> 0
                            magnitude > 1000f -> 100
                            else -> (((magnitude - 20f) / (1000f - 20f)) * 100f).toInt()
                        }

                        Log.e(
                            "SensorPage",
                            "Magnetic field: ${magnitude.toInt()} uT, gauge: $normalizedValue%"
                        )

                        magneticGauge = normalizedValue
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        if (magneticSensor != null) {
            sensorManager.registerListener(
                magneticSensorListener,
                magneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d("SensorPage", "Start listening to magnetic field sensor")
        }

        onDispose {
            if (magneticSensor != null) {
                sensorManager.unregisterListener(magneticSensorListener, magneticSensor)
                Log.d("SensorPage", "Stop listening to magnetic field sensor")
            }
        }
    }

    // 将 0-100 的读数映射到 -135° 到 +135°，Compose 中正角度即顺时针旋转。
    val normalizedGauge = magneticGauge.coerceIn(0, 100) / 100f
    val targetRotationAngle = GAUGE_START_ANGLE + normalizedGauge * GAUGE_SWEEP_ANGLE

    val rotationAngle by animateFloatAsState(
        targetValue = targetRotationAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pointerRotation"
    )

    val pointerWidth = 48.dp
    val pointerHeight = 99.dp

    // Image 默认以自身中心点对齐到父容器中心，这里把图片向上挪一点，
    // 让“圆球中心”而不是“图片中心”落在仪表盘中心。
    val pointerPivotOffsetY = (pointerHeight.value * (0.5f - POINTER_PIVOT_Y)).dp

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8095FF)).statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
            Image(
                painter = painterResource(R.drawable.svg_back),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart).clickable {
                    context.findBaseActivityVBind()?.finish()
                }
            )
            Text(
                "Magnetometer",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .offset(y = (-60).dp)
        ) {
            Image(
                painter = painterResource(R.mipmap.img_circular_arc_2),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().align(Alignment.TopCenter),
                contentDescription = null
            )

            Image(
                painter = painterResource(R.mipmap.img_circular_pointer_2),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = pointerWidth, height = pointerHeight)
                    .offset(y = pointerPivotOffsetY)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(POINTER_PIVOT_X, POINTER_PIVOT_Y)
                        rotationZ = rotationAngle
                    },
                contentScale = ContentScale.Fit,
                contentDescription = null
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 17.dp)
                    .width(134.dp)
                    .height(66.dp)
                    .background(color = Color(0xFFF5D836), shape = RoundedCornerShape(35.dp))
            ) {
                Text(
                    "$magneticGauge",
                    color = Color(0xFF152946),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.mipmap.img_magnetic_icon),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Detecting magnetic field signal...",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                "A stronger signal indicates that the device is closer or transmitting data",
                color = White50,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 55.dp)
            )
        }
    }
}
