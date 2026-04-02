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

                        // 计算磁场强度（微特斯拉）
                        val magnitude = sqrt(x * x + y * y + z * z)

                        // 将磁场强度转换为0-100的百分比值
                        // 地球磁场通常在25-65 μT之间，我们设置一个合理的范围
                        val normalizedValue = when {
                            magnitude < 20 -> 0 // 低于20 μT认为是异常低
                            magnitude > 1000 -> 100 // 高于200 μT认为是强磁场
                            else -> ((magnitude - 20) / (1000 - 20) * 100).toInt()
                        }

                        Log.e("SensorPage", "磁场强度：${magnitude.toInt()} μT，百分比：$normalizedValue%")

                        magneticGauge = normalizedValue
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    // 根据isListening状态注册或取消注册传感器监听
    DisposableEffect(Unit) {
        if (magneticSensor != null) {
            sensorManager.registerListener(
                magneticSensorListener,
                magneticSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d("SensorPage", "开始监听磁场传感器")
        }

        onDispose {
            if (magneticSensor != null) {
                sensorManager.unregisterListener(magneticSensorListener, magneticSensor)
                Log.d("SensorPage", "停止监听磁场传感器")
            }
        }
    }

    // 计算旋转角度并添加动画
    // 指针切图默认朝向上90度
    // 我们需要顺时针旋转：
    // 0%时：指针朝向左下45度（从朝上顺时针旋转225度）
    // 100%时：指针朝向右下45度（从朝上顺时针旋转315度或-45度）
    val targetRotationAngle = if (magneticGauge == 0) {
        // 0%时：顺时针旋转225度，使指针指向左下方45度
        225f
    } else {
        // 百分比值转换为角度：初始225度 + 顺时针旋转（每1%旋转0.9度）
        // 从225度到315度总共90度的旋转范围
        225f + (magneticGauge.toFloat() * 0.9f)
    }

    val rotationAngle by animateFloatAsState(
        targetValue = targetRotationAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pointerRotation"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF8095FF)).statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
            Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                context.findBaseActivityVBind()?.finish()
            })
            Text("Magnetometer", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
        }

        Box(modifier = Modifier
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
                    .offset(x = 55.dp)
                    .offset(y = (-45).dp)
                    .graphicsLayer {
                        // 设置旋转中心为左下角 (0f, 1f)
                        // (0,0) 是左上角，(1,1) 是右下角
                        transformOrigin = TransformOrigin(0f, 1f)
                        rotationZ = rotationAngle
                    },
                contentDescription = null
            )

            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 17.dp)
                .width(134.dp)
                .height(66.dp)
                .background(color = Color(0xFFF5D836), shape = RoundedCornerShape(35.dp))
            ) {
                Text("$magneticGauge", color = Color(0xFF152946), fontSize = 30.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.mipmap.img_magnetic_icon), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("Detecting magnetic field signal...", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(5.dp))
            Text("A stronger signal indicates that the device is closer or transmitting data", color = White50, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 55.dp))
        }
    }
}