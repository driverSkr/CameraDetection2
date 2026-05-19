package com.findhiddencamera.spycameralocator.ui.bluetooth.page

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import androidx.core.app.ActivityCompat
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.model.DetectBluetoothDevice
import com.findhiddencamera.spycameralocator.room.DetectDataBase
import com.findhiddencamera.spycameralocator.ui.bluetooth.view.RadarScannerWithControls3
import com.findhiddencamera.spycameralocator.ui.result.BluetoothScanResultActivity
import com.findhiddencamera.spycameralocator.ui.subscribe.GuideSubscribeActivity
import com.findhiddencamera.spycameralocator.ui.wifi.view.RandomRedDotsWithVisibility
import com.findhiddencamera.spycameralocator.utils.BluetoothHelper
import com.findhiddencamera.spycameralocator.utils.SubscribeHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.roundToInt

private const val SIMULATED_SCAN_PROGRESS_CAP = 94
private const val BLUETOOTH_SCAN_TIMEOUT_MS = 12_000L
private const val BLUETOOTH_RESULT_FALLBACK_TIMEOUT_MS = 15_000L
private const val PROGRESS_ANIMATION_DURATION_MS = 650
private const val SCAN_COMPLETION_HOLD_DELAY_MS = 250L

private val BLUETOOTH_SCAN_STAGES = listOf(
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

@SuppressLint("MissingPermission")
@Composable
fun BluetoothCamerasPage() {
    val context = LocalContext.current
    val isAnimating = remember { mutableStateOf(true) }
    val suspiciousDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val trustedDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val detectProgress = remember { mutableIntStateOf(0) }
    val foundDeviceTarget = remember { mutableIntStateOf(0) }
    val scanCompleted = remember { mutableStateOf(false) }
    val displayedProgress = remember { Animatable(0f) }
    var hasNavigated by remember { mutableStateOf(false) }

    val animatedFoundCount by animateIntAsState(
        targetValue = foundDeviceTarget.intValue,
        animationSpec = tween(durationMillis = PROGRESS_ANIMATION_DURATION_MS, easing = FastOutSlowInEasing),
        label = "bluetoothFoundDeviceCount"
    )
    val displayedProgressPercent = (displayedProgress.value * 100f).roundToInt()

    val localBluetoothMac = remember {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.address ?: ""
    }

    LaunchedEffect(Unit) {
        simulateScanProgress(detectProgress, isAnimating)
    }

    LaunchedEffect(Unit) {
        startBluetoothScan(
            context = context,
            localBluetoothMac = localBluetoothMac,
            suspiciousDevices = suspiciousDevices,
            trustedDevices = trustedDevices,
            detectProgress = detectProgress,
            foundDeviceTarget = foundDeviceTarget,
            scanCompleted = scanCompleted
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
        delay(BLUETOOTH_RESULT_FALLBACK_TIMEOUT_MS)
        if (!scanCompleted.value && detectProgress.intValue < 100) {
            finishBluetoothScan(detectProgress, scanCompleted)
        }
    }

    LaunchedEffect(scanCompleted.value) {
        if (!hasNavigated && scanCompleted.value) {
            hasNavigated = true

            // 先让用户明确看到 100%，再停止雷达并进入结果页。
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
            DetectDataBase.invoke(context).getBluetoothDao().addDevice(
                DetectBluetoothDevice(
                    createTime = scanTimeSeconds,
                    suspiciousDevices = suspiciousDevicesList,
                    trustedDevices = trustedDevicesList
                )
            )
            // 检测记录先入库，再根据订阅状态决定是否展示结果引导订阅页。
            if (SubscribeHelper.isSubscribed) {
                BluetoothScanResultActivity.launch(context, suspiciousDevicesList, trustedDevicesList, scanTimeSeconds)
            } else {
                GuideSubscribeActivity.launchForBluetooth(context, suspiciousDevicesList, trustedDevicesList, scanTimeSeconds)
            }
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
                "Bluetooth Cameras",
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
                RadarScannerWithControls3(isAnimating)
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

        BluetoothScanProgressBar(
            progress = displayedProgress.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        BLUETOOTH_SCAN_STAGES.forEachIndexed { index, title ->
            val stageNumber = index + 1
            val visualStage = when {
                displayedProgressPercent >= 100 -> BLUETOOTH_SCAN_STAGES.size + 1
                else -> stageForProgress(displayedProgressPercent)
            }
            val status = when {
                visualStage > stageNumber -> ScanStageStatus.Completed
                visualStage == stageNumber -> ScanStageStatus.Active
                else -> ScanStageStatus.Pending
            }

            BluetoothScanStageRow(
                title = title,
                status = status,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            if (index != BLUETOOTH_SCAN_STAGES.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun BluetoothScanProgressBar(
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
private fun BluetoothScanStageRow(
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

@SuppressLint("MissingPermission")
private fun startBluetoothScan(
    context: Context,
    localBluetoothMac: String,
    suspiciousDevices: SnapshotStateList<BluetoothDevice>,
    trustedDevices: SnapshotStateList<BluetoothDevice>,
    detectProgress: MutableIntState,
    foundDeviceTarget: MutableIntState,
    scanCompleted: MutableState<Boolean>
) {
    val handler = Handler(Looper.getMainLooper())
    val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = manager.adapter
    val leScanner = bluetoothAdapter?.bluetoothLeScanner

    if (bluetoothAdapter == null) {
        finishBluetoothScan(detectProgress, scanCompleted)
        return
    }

    suspiciousDevices.clear()
    trustedDevices.clear()
    detectProgress.intValue = max(detectProgress.intValue, 1)

    addMine(bluetoothAdapter, trustedDevices)
    refreshFoundDeviceTarget(foundDeviceTarget, suspiciousDevices, trustedDevices)

    scanClassicBluetooth(
        context = context,
        handler = handler,
        bluetoothAdapter = bluetoothAdapter,
        localBluetoothMac = localBluetoothMac,
        suspiciousDevices = suspiciousDevices,
        trustedDevices = trustedDevices,
        onDeviceChanged = {
            refreshFoundDeviceTarget(foundDeviceTarget, suspiciousDevices, trustedDevices)
        }
    )

    if (leScanner != null) {
        scanLeBluetooth(
            context = context,
            handler = handler,
            leScanner = leScanner,
            localBluetoothMac = localBluetoothMac,
            suspiciousDevices = suspiciousDevices,
            trustedDevices = trustedDevices,
            onDeviceChanged = {
                refreshFoundDeviceTarget(foundDeviceTarget, suspiciousDevices, trustedDevices)
            }
        )
    }

    handler.postDelayed({
        stopBluetoothScan(bluetoothAdapter)
        finishBluetoothScan(detectProgress, scanCompleted)
    }, BLUETOOTH_SCAN_TIMEOUT_MS)
}

@SuppressLint("MissingPermission")
fun addMine(
    bluetoothAdapter: BluetoothAdapter,
    trustedDevices: SnapshotStateList<BluetoothDevice>
) {
    val bluetoothName = bluetoothAdapter.name ?: "Unknown"
    val bluetoothAddress = bluetoothAdapter.address ?: "Unknown"

    val myDevice = BluetoothDevice(
        name = bluetoothName,
        type = "Phone",
        mac = bluetoothAddress,
        iconRes = BluetoothHelper.getDeviceIcon("Phone", 0),
        signal = 100,
        signalColor = BluetoothHelper.getSignalColor(100),
        uuid = "",
        connected = true,
        rssi = 0,
        riskLevel = 0
    )
    trustedDevices.clear()
    trustedDevices.add(0, myDevice)
}

@SuppressLint("MissingPermission")
private fun scanClassicBluetooth(
    context: Context,
    handler: Handler,
    bluetoothAdapter: BluetoothAdapter,
    localBluetoothMac: String,
    suspiciousDevices: SnapshotStateList<BluetoothDevice>,
    trustedDevices: SnapshotStateList<BluetoothDevice>,
    onDeviceChanged: () -> Unit
) {
    if (!hasScanPermission(context)) {
        return
    }

    bluetoothAdapter.bondedDevices.orEmpty().forEach { device ->
        addBluetoothDeviceOnMain(
            handler = handler,
            device = device,
            rssi = null,
            localBluetoothMac = localBluetoothMac,
            suspiciousDevices = suspiciousDevices,
            trustedDevices = trustedDevices,
            onDeviceChanged = onDeviceChanged
        )
    }

    bluetoothAdapter.startDiscovery()

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (android.bluetooth.BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(
                    android.bluetooth.BluetoothDevice.EXTRA_DEVICE
                )
                val rssi = intent.getShortExtra(
                    android.bluetooth.BluetoothDevice.EXTRA_RSSI,
                    Short.MIN_VALUE
                ).toInt()

                if (device != null) {
                    addBluetoothDeviceOnMain(
                        handler = handler,
                        device = device,
                        rssi = rssi,
                        localBluetoothMac = localBluetoothMac,
                        suspiciousDevices = suspiciousDevices,
                        trustedDevices = trustedDevices,
                        onDeviceChanged = onDeviceChanged
                    )
                }
            }
        }
    }

    context.registerReceiver(receiver, IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND))
    handler.postDelayed({
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
    }, BLUETOOTH_SCAN_TIMEOUT_MS)
}

@SuppressLint("MissingPermission")
private fun scanLeBluetooth(
    context: Context,
    handler: Handler,
    leScanner: BluetoothLeScanner,
    localBluetoothMac: String,
    suspiciousDevices: SnapshotStateList<BluetoothDevice>,
    trustedDevices: SnapshotStateList<BluetoothDevice>,
    onDeviceChanged: () -> Unit
) {
    if (!hasScanPermission(context)) {
        return
    }

    val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                addBluetoothDeviceOnMain(
                    handler = handler,
                    device = device,
                    rssi = result.rssi,
                    localBluetoothMac = localBluetoothMac,
                    suspiciousDevices = suspiciousDevices,
                    trustedDevices = trustedDevices,
                    onDeviceChanged = onDeviceChanged
                )
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results.orEmpty().forEach { result ->
                addBluetoothDeviceOnMain(
                    handler = handler,
                    device = result.device,
                    rssi = result.rssi,
                    localBluetoothMac = localBluetoothMac,
                    suspiciousDevices = suspiciousDevices,
                    trustedDevices = trustedDevices,
                    onDeviceChanged = onDeviceChanged
                )
            }
        }
    }

    leScanner.startScan(leScanCallback)
    handler.postDelayed({
        try {
            leScanner.stopScan(leScanCallback)
        } catch (_: Exception) {
        }
    }, BLUETOOTH_SCAN_TIMEOUT_MS)
}

private fun addBluetoothDeviceOnMain(
    handler: Handler,
    device: android.bluetooth.BluetoothDevice,
    rssi: Int?,
    localBluetoothMac: String,
    suspiciousDevices: SnapshotStateList<BluetoothDevice>,
    trustedDevices: SnapshotStateList<BluetoothDevice>,
    onDeviceChanged: () -> Unit
) {
    handler.post {
        val beforeCount = suspiciousDevices.size + trustedDevices.size
        BluetoothHelper.addDevice(
            device = device,
            rssi = rssi,
            localBluetoothMac = localBluetoothMac,
            suspiciousDevices = suspiciousDevices,
            trustedDevices = trustedDevices
        )
        if (suspiciousDevices.size + trustedDevices.size != beforeCount) {
            onDeviceChanged()
        }
    }
}

private fun refreshFoundDeviceTarget(
    foundDeviceTarget: MutableIntState,
    suspiciousDevices: SnapshotStateList<BluetoothDevice>,
    trustedDevices: SnapshotStateList<BluetoothDevice>
) {
    foundDeviceTarget.intValue = max(
        foundDeviceTarget.intValue,
        suspiciousDevices.size + trustedDevices.size
    )
}

@SuppressLint("MissingPermission")
private fun stopBluetoothScan(
    bluetoothAdapter: BluetoothAdapter
) {
    try {
        bluetoothAdapter.cancelDiscovery()
    } catch (_: Exception) {
    }
}

private fun finishBluetoothScan(
    detectProgress: MutableIntState,
    scanCompleted: MutableState<Boolean>
) {
    detectProgress.intValue = 100
    scanCompleted.value = true
}

private fun hasScanPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
