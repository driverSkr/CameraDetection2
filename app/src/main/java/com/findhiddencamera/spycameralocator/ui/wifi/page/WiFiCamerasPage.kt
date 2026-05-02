package com.findhiddencamera.spycameralocator.ui.wifi.page

import android.content.Context
import android.net.wifi.WifiManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.DetectWifiDevice
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.findhiddencamera.spycameralocator.room.DetectDataBase
import com.findhiddencamera.spycameralocator.ui.result.WifiDetectResultActivity
import com.findhiddencamera.spycameralocator.ui.wifi.view.RadarScannerWithControls2
import com.findhiddencamera.spycameralocator.ui.wifi.view.RandomRedDotsWithVisibility
import com.findhiddencamera.spycameralocator.utils.WifiHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.subnet.Device
import java.util.Collections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.math.max
import kotlin.math.roundToInt

private const val SIMULATED_SCAN_PROGRESS_CAP = 94
private const val SCAN_RESULT_FALLBACK_TIMEOUT_MS = 25_000L
private const val PROGRESS_ANIMATION_DURATION_MS = 650
private const val SCAN_COMPLETION_HOLD_DELAY_MS = 250L

private val WIFI_SCAN_STAGES = listOf(
    "Camera Detector Ready...",
    "Detecting Suspicious Devices...",
    "Analyzing Device Ports...",
    "Identifying Camera...",
    "Collate Detection Results..."
)

private enum class ScanStageStatus {
    Completed,
    Active,
    Pending
}

@Composable
fun WiFiCamerasPage() {
    val context = LocalContext.current
    val scanScope = rememberCoroutineScope()
    val isAnimating = remember { mutableStateOf(true) }
    val suspiciousDevices = remember { mutableStateListOf<WifiDevice>() }
    val trustedDevices = remember { mutableStateListOf<WifiDevice>() }
    val detectProgress = remember { mutableIntStateOf(0) }
    val foundDeviceTarget = remember { mutableIntStateOf(0) }
    val scanCompleted = remember { mutableStateOf(false) }
    val displayedProgress = remember { Animatable(0f) }
    var hasNavigated by remember { mutableStateOf(false) }

    val animatedFoundCount by animateIntAsState(
        targetValue = foundDeviceTarget.intValue,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "foundDeviceCount"
    )
    val displayedProgressPercent = (displayedProgress.value * 100f).roundToInt()

    val localIp = remember {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        String.format(
            "%d.%d.%d.%d",
            wifiInfo.ipAddress and 0xff,
            wifiInfo.ipAddress shr 8 and 0xff,
            wifiInfo.ipAddress shr 16 and 0xff,
            wifiInfo.ipAddress shr 24 and 0xff
        )
    }

    LaunchedEffect(Unit) {
        simulateScanProgress(detectProgress, isAnimating)
    }

    LaunchedEffect(Unit) {
        wifiDetect(
            localIp = localIp,
            suspiciousDevices = suspiciousDevices,
            trustedDevices = trustedDevices,
            detectProgress = detectProgress,
            foundDeviceTarget = foundDeviceTarget,
            scanCompleted = scanCompleted,
            scanScope = scanScope
        )
    }

    LaunchedEffect(detectProgress.intValue, scanCompleted.value) {
        if (!scanCompleted.value) {
            displayedProgress.animateTo(
                targetValue = detectProgress.intValue.coerceIn(0, 100) / 100f,
                animationSpec = tween(
                    durationMillis = PROGRESS_ANIMATION_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(SCAN_RESULT_FALLBACK_TIMEOUT_MS)
        if (!scanCompleted.value && detectProgress.intValue < 100) {
            finishWifiScan(detectProgress, scanCompleted)
        }
    }

    LaunchedEffect(scanCompleted.value) {
        if (!hasNavigated && scanCompleted.value) {
            hasNavigated = true
            displayedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = PROGRESS_ANIMATION_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
            isAnimating.value = false
            delay(SCAN_COMPLETION_HOLD_DELAY_MS)

            val suspiciousDevicesList = ArrayList(suspiciousDevices.toList())
            val trustedDevicesList = ArrayList(trustedDevices.toList())
            val scanTimeSeconds = System.currentTimeMillis().div(1000)
            DetectDataBase.invoke(context).getWifiDao().addDevice(
                DetectWifiDevice(
                    createTime = scanTimeSeconds,
                    suspiciousDevices = suspiciousDevicesList,
                    trustedDevices = trustedDevicesList
                )
            )
            WifiDetectResultActivity.launch(context, suspiciousDevicesList, trustedDevicesList, scanTimeSeconds)
            context.findBaseActivityVBind()?.finish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
            Image(
                painter = painterResource(R.drawable.svg_back),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart).clickable {
                    context.findBaseActivityVBind()?.finish()
                }
            )
            Text(
                "WiFi Cameras",
                color = Color(0xFF152946),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                RadarScannerWithControls2(isAnimating)
                RandomRedDotsWithVisibility(
                    modifier = Modifier.align(Alignment.Center),
                    isAnimating = isAnimating
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Found Devices: ", color = Color(0xFF152946), fontSize = 16.sp)
                Text(
                    "$animatedFoundCount",
                    color = Color(0xFF152946),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(42.dp))

        WifiScanProgressBar(
            progress = displayedProgress.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        WIFI_SCAN_STAGES.forEachIndexed { index, title ->
            val stageNumber = index + 1
            val visualStage = when {
                displayedProgressPercent >= 100 -> WIFI_SCAN_STAGES.size + 1
                else -> stageForProgress(displayedProgressPercent)
            }
            val status = when {
                visualStage > stageNumber -> ScanStageStatus.Completed
                visualStage == stageNumber -> ScanStageStatus.Active
                else -> ScanStageStatus.Pending
            }

            WifiScanStageRow(
                title = title,
                status = status,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            if (index != WIFI_SCAN_STAGES.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun WifiScanProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val progressColor = Color(0xFF8095FF)
    val trackColor = Color(0xFFF0F2FF)
    val currentProgress = progress.coerceIn(0f, 1f)
    val textMeasurer = rememberTextMeasurer()
    val progressText = "${(currentProgress * 100f).roundToInt()}%"
    val textLayoutResult = textMeasurer.measure(
        text = progressText,
        style = TextStyle(
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    )

    Canvas(modifier = modifier.height(24.dp)) {
        val trackHeight = 7.dp.toPx()
        val trackTop = (size.height - trackHeight) / 2f
        val cornerRadius = CornerRadius(trackHeight / 2f, trackHeight / 2f)
        val progressWidth = size.width * currentProgress

        drawRoundRect(
            color = trackColor,
            topLeft = Offset(0f, trackTop),
            size = Size(size.width, trackHeight),
            cornerRadius = cornerRadius
        )

        if (progressWidth > 0f) {
            drawRoundRect(
                color = progressColor,
                topLeft = Offset(0f, trackTop),
                size = Size(progressWidth, trackHeight),
                cornerRadius = cornerRadius
            )
        }

        val bubbleWidth = 34.dp.toPx()
        val bubbleHeight = 18.dp.toPx()
        val bubbleCenterX = progressWidth.coerceIn(bubbleWidth / 2f, size.width - bubbleWidth / 2f)
        val bubbleTop = (size.height - bubbleHeight) / 2f
        val bubbleLeft = bubbleCenterX - bubbleWidth / 2f

        drawRoundRect(
            color = progressColor,
            topLeft = Offset(bubbleLeft, bubbleTop),
            size = Size(bubbleWidth, bubbleHeight),
            cornerRadius = CornerRadius(bubbleHeight / 2f, bubbleHeight / 2f)
        )

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                x = bubbleLeft + (bubbleWidth - textLayoutResult.size.width) / 2f,
                y = bubbleTop + (bubbleHeight - textLayoutResult.size.height) / 2f
            )
        )
    }
}

@Composable
private fun WifiScanStageRow(
    title: String,
    status: ScanStageStatus,
    modifier: Modifier = Modifier
) {
    val textColor = when (status) {
        ScanStageStatus.Completed,
        ScanStageStatus.Active -> Color(0xFF152946)
        ScanStageStatus.Pending -> Color(0xFF9DA9BB)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = textColor,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        when (status) {
            ScanStageStatus.Completed -> {
                Image(
                    painter = painterResource(R.drawable.svg_selected),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            ScanStageStatus.Active -> {
                CircularProgressIndicator(
                    color = Color(0xFF8095FF),
                    trackColor = Color(0xFFE8ECF7),
                    strokeWidth = 1.6.dp,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.size(16.dp)
                )
            }
            ScanStageStatus.Pending -> {
                Canvas(modifier = Modifier.size(16.dp)) {
                    drawCircle(
                        color = Color(0xFFAAB5C8),
                        radius = size.minDimension / 2f - 1.dp.toPx(),
                        style = Stroke(width = 1.4.dp.toPx())
                    )
                }
            }
        }
    }
}

private suspend fun simulateScanProgress(
    detectProgress: MutableIntState,
    isAnimating: MutableState<Boolean>
) {
    detectProgress.intValue = 1

    while (isAnimating.value && detectProgress.intValue < SIMULATED_SCAN_PROGRESS_CAP) {
        delay(260)

        val currentProgress = detectProgress.intValue
        val step = when {
            currentProgress < 24 -> 3
            currentProgress < 56 -> 2
            else -> 1
        }

        detectProgress.intValue = (currentProgress + step).coerceAtMost(SIMULATED_SCAN_PROGRESS_CAP)
    }
}

private fun stageForProgress(progress: Int): Int {
    return when {
        progress >= 92 -> 5
        progress >= 72 -> 4
        progress >= 48 -> 3
        progress >= 18 -> 2
        progress > 0 -> 1
        else -> 0
    }
}

private fun wifiDetect(
    localIp: String,
    suspiciousDevices: SnapshotStateList<WifiDevice>,
    trustedDevices: SnapshotStateList<WifiDevice>,
    detectProgress: MutableIntState,
    foundDeviceTarget: MutableIntState,
    scanCompleted: MutableState<Boolean>,
    scanScope: CoroutineScope
) {
    val discoveredIps = Collections.synchronizedSet(mutableSetOf<String>())

    scanScope.launch {
        detectProgress.intValue = max(detectProgress.intValue, 1)
    }

    try {
        SubnetDevices.fromLocalAddress().findDevices(object : SubnetDevices.OnSubnetDeviceFound {
            override fun onDeviceFound(device: Device?) {
                val ip = device?.ip ?: return
                if (discoveredIps.add(ip)) {
                    scanScope.launch {
                        foundDeviceTarget.intValue = max(foundDeviceTarget.intValue, discoveredIps.size)
                    }
                }
            }

            override fun onFinished(devicesFound: ArrayList<Device?>?) {
                val finalDevices = devicesFound
                    .orEmpty()
                    .filterNotNull()
                    .filter { it.ip.isNotBlank() }
                    .distinctBy { it.ip }

                scanScope.launch {
                    foundDeviceTarget.intValue = max(foundDeviceTarget.intValue, finalDevices.size)

                    if (finalDevices.isEmpty()) {
                        finishWifiScan(detectProgress, scanCompleted)
                        return@launch
                    }

                    val semaphore = Semaphore(permits = 6)
                    val revealDelayMillis = when {
                        finalDevices.size > 60 -> 35L
                        finalDevices.size > 30 -> 55L
                        else -> 110L
                    }
                    val deviceDetections = finalDevices.map { device ->
                        async(Dispatchers.IO) {
                            semaphore.withPermit {
                                runCatching {
                                    WifiHelper.detectDeviceType(device, localIp)
                                }.getOrNull()
                            }
                        }
                    }

                    deviceDetections.forEachIndexed { index, deviceDetection ->
                        val wifiDevice = deviceDetection.await()
                        if (wifiDevice != null) {
                            if (wifiDevice.riskLevel > 0) {
                                suspiciousDevices.add(wifiDevice)
                            } else {
                                trustedDevices.add(wifiDevice)
                            }
                        }

                        foundDeviceTarget.intValue = max(foundDeviceTarget.intValue, index + 1)
                        delay(revealDelayMillis)
                    }

                    finishWifiScan(detectProgress, scanCompleted)
                }
            }
        })
    } catch (_: Exception) {
        scanScope.launch {
            finishWifiScan(detectProgress, scanCompleted)
        }
    }
}

private fun finishWifiScan(
    detectProgress: MutableIntState,
    scanCompleted: MutableState<Boolean>
) {
    detectProgress.intValue = 100
    scanCompleted.value = true
}
