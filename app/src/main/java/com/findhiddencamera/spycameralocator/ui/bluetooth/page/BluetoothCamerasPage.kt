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
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.BluetoothDevice
import com.findhiddencamera.spycameralocator.model.DetectBluetoothDevice
import com.findhiddencamera.spycameralocator.room.DetectDataBase
import com.findhiddencamera.spycameralocator.ui.bluetooth.view.RadarScannerWithControls3
import com.findhiddencamera.spycameralocator.ui.result.BluetoothScanResultActivity
import com.findhiddencamera.spycameralocator.utils.BluetoothHelper
import com.findhiddencamera.spycameralocator.utils.findBaseActivityVBind

@Composable
fun BluetoothCamerasPage() {
    val context = LocalContext.current
    val isAnimating = remember { mutableStateOf(true) }
    val suspiciousDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val trustedDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val localBluetoothMac = remember {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothAdapter?.address ?: ""
    }

    val loadingState = remember { MutableLiveData(0) }
    val currentLoadingState by loadingState.observeAsState(0)

    LaunchedEffect(Unit) {
        startBluetoothScan(context, isAnimating, localBluetoothMac, suspiciousDevices, trustedDevices, loadingState)
    }

    LaunchedEffect(isAnimating.value) {
        if (!isAnimating.value) {
            val suspiciousDevicesList = ArrayList(suspiciousDevices.toList())
            val trustedDevicesList = ArrayList(trustedDevices.toList())
            DetectDataBase.invoke(context).getBluetoothDao().addDevice(DetectBluetoothDevice(suspiciousDevices = suspiciousDevices, trustedDevices = trustedDevices))
            BluetoothScanResultActivity.launch(context, suspiciousDevicesList, trustedDevicesList)
            context.findBaseActivityVBind()?.finish()
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, top = 9.dp)) {
            Image(painter = painterResource(R.drawable.svg_back), contentDescription = null, modifier = Modifier.align(Alignment.CenterStart).clickable{
                context.findBaseActivityVBind()?.finish()
            })
            Text("Bluetooth Cameras", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
            RadarScannerWithControls3(isAnimating)
            Row(modifier = Modifier.align(Alignment.BottomCenter), verticalAlignment = Alignment.CenterVertically) {
                Text("Found Devices:", color = Color(0xFF152946), fontSize = 16.sp)
                Text("${suspiciousDevices.size + trustedDevices.size}", color = Color(0xFF152946), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).padding(horizontal = 30.dp).background(color = Color(0xFF5874FF)))

        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Camera Detector Ready...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 1) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Detecting Suspicious Devices...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 2) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Analyzing Device Ports...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 3) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Identifying Camera...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 4) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Collate Detection Results...", color = Color(0xFF152946), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            if (currentLoadingState >= 5) {
                Image(painter = painterResource(R.drawable.svg_selected), contentDescription = null)
            } else {
                CircularProgressIndicator(color = Color(0xFF152946), trackColor = Color(0xFF152946).copy(0.3f), strokeWidth = 1.5.dp, strokeCap = StrokeCap.Round, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun startBluetoothScan(context: Context, isAnimating: MutableState<Boolean>, localBluetoothMac: String, suspiciousDevices: SnapshotStateList<BluetoothDevice>, trustedDevices: SnapshotStateList<BluetoothDevice>, loadingState: MutableLiveData<Int>) {
    val handler = Handler(Looper.getMainLooper())
    val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = manager.adapter
    val leScanner = bluetoothAdapter!!.bluetoothLeScanner
    suspiciousDevices.clear()
    trustedDevices.clear()
    loadingState.postValue(1)
    addMine(bluetoothAdapter, trustedDevices)
    loadingState.postValue(2)
    // 经典蓝牙
    scanClassicBluetooth(context, handler, bluetoothAdapter, localBluetoothMac, suspiciousDevices, trustedDevices)
    loadingState.postValue(3)
    // BLE
    scanLeBluetooth(context, handler, leScanner, localBluetoothMac, suspiciousDevices, trustedDevices)
    loadingState.postValue(5)
    // 定时停止
    handler.postDelayed({
        try {
            bluetoothAdapter.cancelDiscovery()
        } catch (_: Exception) {
        }
        // todo 保存到本地
        // todo 跳转
        isAnimating.value = false
        loadingState.postValue(5)
    }, 12000L)
}

@SuppressLint("MissingPermission")
fun addMine(bluetoothAdapter: BluetoothAdapter, trustedDevices: SnapshotStateList<BluetoothDevice>) {

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

private fun scanClassicBluetooth(context: Context, handler: Handler, bluetoothAdapter: BluetoothAdapter, localBluetoothMac: String, suspiciousDevices: SnapshotStateList<BluetoothDevice>, trustedDevices: SnapshotStateList<BluetoothDevice>) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    val paired = bluetoothAdapter.bondedDevices ?: emptySet()
    for (device in paired) {
        BluetoothHelper.addDevice(device, rssi = null, localBluetoothMac, suspiciousDevices, trustedDevices)
    }
    bluetoothAdapter.startDiscovery()
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (android.bluetooth.BluetoothDevice.ACTION_FOUND == action) {
                val device =
                    intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                val rssi =
                    intent.getShortExtra(android.bluetooth.BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                if (device != null) BluetoothHelper.addDevice(device, rssi, localBluetoothMac, suspiciousDevices, trustedDevices)
            }
        }
    }
    val filter = IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(receiver, filter)
    handler.postDelayed({
        try { context.unregisterReceiver(receiver) } catch (_: Exception) { }
    }, 12000L)
}

private fun scanLeBluetooth(context: Context, handler: Handler, leScanner: BluetoothLeScanner, localBluetoothMac: String, suspiciousDevices: SnapshotStateList<BluetoothDevice>, trustedDevices: SnapshotStateList<BluetoothDevice>) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { BluetoothHelper.addDevice(it, result.rssi, localBluetoothMac, suspiciousDevices, trustedDevices) }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { BluetoothHelper.addDevice(it.device, it.rssi, localBluetoothMac, suspiciousDevices, trustedDevices) }
        }
    }
    leScanner.startScan(leScanCallback)
    handler.postDelayed({ leScanner.stopScan(leScanCallback) }, 12000L)
}