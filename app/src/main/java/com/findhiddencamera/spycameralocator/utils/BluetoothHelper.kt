package com.findhiddencamera.spycameralocator.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.findhiddencamera.spycameralocator.R

object BluetoothHelper {

    fun requiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun hasBluetoothPermission(context: Context): Boolean {
        return AppPermissionHelper.hasPermissions(context, requiredPermissions())
    }

    /**
     * 检查并请求蓝牙权限
     * @param context 上下文
     * @param bluetoothPermissionLauncher 权限请求的Launcher
     * @param onResult 可选的回调，返回是否所有必要权限都已授予
     */
    fun checkBluetoothPermission(
        context: Context,
        bluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>,
        onResult: ((Boolean) -> Unit)? = null
    ) {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ 需要请求新权限
            val hasBluetoothScan = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED

            val hasBluetoothConnect = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

            if (!hasBluetoothScan) { permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN) }
            if (!hasBluetoothConnect) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            // Android 11 及以下需要位置权限
            val hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (!hasLocationPermission) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            // 需要请求权限
            bluetoothPermissionLauncher.launch(permissionsToRequest.toTypedArray())
            onResult?.invoke(false)
        } else {
            // 已有所有权限
            onResult?.invoke(true)
        }
    }

    fun getSignalColor(signal: Int): Int {
        return when {
            signal >= 60 -> Color.parseColor("#FFF54F36")
            signal >= 40 -> Color.parseColor("#FFF5C836")
            signal >= 20 -> Color.parseColor("#FF05CA67")
            else -> Color.parseColor("#80FFFFFF")
        }
    }

    fun getDeviceIcon(type: String, riskLevel: Int): Int {
        val prefix = if (riskLevel > 0) "ic_s_" else "ic_c_"

        return when (type.lowercase()) {
            "phone" -> getIconResource("${prefix}phone")
            "headphone" -> getIconResource("${prefix}headphone")
            "speaker" -> getIconResource("${prefix}speaker")
            "watch" -> getIconResource("${prefix}watch")
            "computer" -> getIconResource("${prefix}computer")
            "camera" -> getIconResource("${prefix}camera")
            "car" -> getIconResource("${prefix}car")
            "tracker" -> getIconResource("${prefix}tracker")
            "ble device" -> getIconResource("${prefix}ble")
            "classic device" -> getIconResource("${prefix}classic")
            "bluetooth device" -> getIconResource("${prefix}bluetooth")
            else -> getIconResource("${prefix}unknown")
        }
    }

    private fun getIconResource(name: String): Int {
        return when (name) {
            "ic_s_phone" -> R.drawable.ic_s_phone
            "ic_c_phone" -> R.drawable.ic_c_phone
            "ic_s_headphone" -> R.drawable.ic_s_phone
            "ic_c_headphone" -> R.drawable.ic_c_phone
            "ic_s_speaker" -> R.drawable.ic_s_phone
            "ic_c_speaker" -> R.drawable.ic_c_phone
            "ic_s_watch" -> R.drawable.ic_s_phone
            "ic_c_watch" -> R.drawable.ic_c_phone
            "ic_s_computer" -> R.drawable.ic_s_computer
            "ic_c_computer" -> R.drawable.ic_c_computer
            "ic_s_camera" -> R.drawable.ic_s_camera
            "ic_c_camera" -> R.drawable.ic_c_camera
            "ic_s_car" -> R.drawable.ic_s_phone
            "ic_c_car" -> R.drawable.ic_c_phone
            "ic_s_tracker" -> R.drawable.ic_s_phone
            "ic_c_tracker" -> R.drawable.ic_c_phone
            "ic_s_ble" -> R.drawable.ic_s_phone
            "ic_c_ble" -> R.drawable.ic_c_phone
            "ic_s_classic" -> R.drawable.ic_s_computer
            "ic_c_classic" -> R.drawable.ic_c_computer
            "ic_s_bluetooth" -> R.drawable.ic_s_phone
            "ic_c_bluetooth" -> R.drawable.ic_c_phone
            "ic_s_unknown" -> R.drawable.ic_s_unknown
            "ic_c_unknown" -> R.drawable.ic_c_unknown
            else -> {
                LogUtils.w("Bluetooth device icon not found: $name, using default icon")
                R.drawable.ic_sub1
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun addDevice(device: BluetoothDevice, rssi: Int?, localBluetoothMac: String, suspiciousDevices: SnapshotStateList<com.findhiddencamera.spycameralocator.model.BluetoothDevice>, trustedDevices: SnapshotStateList<com.findhiddencamera.spycameralocator.model.BluetoothDevice>) {
        val name = device.name
            ?.takeIf { it.isNotBlank() && !it.equals("Unknown", true) }
            ?: "Suspected Devices"
        val mac = device.address ?: ""
        val type = when (device.type) {
            BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic"
            BluetoothDevice.DEVICE_TYPE_LE -> "BLE"
            BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual"
            else -> "Unknown"
        }

        // 只有本机蓝牙是可信，其余全是可疑
        val isLocalDevice = mac.equals(localBluetoothMac, ignoreCase = true)
        val riskLevel = if (isLocalDevice) 0 else 1

        // 分析设备类型和风险等级
        val majorType = device.bluetoothClass?.majorDeviceClass
        val deviceType = when (majorType) {
            BluetoothClass.Device.Major.COMPUTER -> "Computer"
            BluetoothClass.Device.Major.PHONE -> "Phone"
            BluetoothClass.Device.Major.AUDIO_VIDEO -> "Audio/Video"
            BluetoothClass.Device.Major.WEARABLE -> "Wearable"
            BluetoothClass.Device.Major.IMAGING -> "Imaging"
            BluetoothClass.Device.Major.NETWORKING -> "Networking"
            BluetoothClass.Device.Major.PERIPHERAL -> "Peripheral"
            BluetoothClass.Device.Major.TOY -> "Toy"
            BluetoothClass.Device.Major.HEALTH -> "Health"
            else -> analyzeDeviceType(name, type) // 兜底用原有名称分析
        }
        val signalStrength = generateSignalStrength(rssi ?: -100)
        val signalColor = getSignalColor(signalStrength)

        val myDevice = com.findhiddencamera.spycameralocator.model.BluetoothDevice(
            name = name,
            type = deviceType,
            mac = mac,
            iconRes = getDeviceIcon(deviceType, riskLevel),
            signal = signalStrength,
            signalColor = signalColor,
            uuid = "",
            connected = false,
            rssi = rssi ?: -100,
            riskLevel = riskLevel
        )

        val exists = (suspiciousDevices + trustedDevices).any { it.mac == mac }
        if (!exists) {
            if (riskLevel > 0) {
                suspiciousDevices.add(myDevice)
                LogUtils.d("Found suspicious Bluetooth device: $name ($mac)")
            } else {
                // 本机设备插入到可信设备列表第一个
                trustedDevices.add(0, myDevice)
                LogUtils.d("Found trusted Bluetooth device: $name ($mac)")
            }
        }
    }

    private fun analyzeDeviceType(name: String, bluetoothType: String): String {
        val lowerName = name.lowercase()
        return when {
            lowerName.contains("phone") || lowerName.contains("mobile") -> "Phone"
            lowerName.contains("headphone") || lowerName.contains("earbud") || lowerName.contains("airpods") -> "Headphone"
            lowerName.contains("speaker") || lowerName.contains("sound") -> "Speaker"
            lowerName.contains("watch") || lowerName.contains("band") -> "Watch"
            lowerName.contains("keyboard") || lowerName.contains("mouse") -> "Computer"
            lowerName.contains("camera") || lowerName.contains("cam") -> "Camera"
            lowerName.contains("car") || lowerName.contains("vehicle") -> "Car"
            lowerName.contains("tracker") || lowerName.contains("tag") -> "Tracker"
            lowerName.contains("unknown") || lowerName.isEmpty() -> "Unknown"
            bluetoothType == "LE" -> "BLE Device"
            bluetoothType == "Classic" -> "Classic Device"
            else -> "Bluetooth Device"
        }
    }

    private fun generateSignalStrength(rssi: Int): Int {
        return rssi + 100
    }
}
