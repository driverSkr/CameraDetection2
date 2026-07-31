package com.findhiddencamera.spycameralocator.utils

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.findhiddencamera.spycameralocator.R
import com.findhiddencamera.spycameralocator.model.WifiDevice
import com.stealthcopter.networktools.PortScan
import com.stealthcopter.networktools.subnet.Device
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object WifiHelper {

    fun requiredPermissions(): Array<String> {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        return permissions.toTypedArray()
    }

    private val commonPorts = listOf(
        22,    // TCP - PC - SSH 服务常见于 Linux/PC 系统
        23,    // TCP - Router - 老式路由器常启用 Telnet 管理
        53,    // UDP - Router - 路由器通常内建 DNS 服务
        80,    // TCP - Router - 路由器默认 Web 控制台端口
        443,   // TCP - Router - HTTPS 管理页面
        139,   // TCP - PC - Windows 文件共享（NetBIOS）
        445,   // TCP - PC - Windows SMB 文件共享
        554,   // TCP - Camera - RTSP 视频流媒体，典型监控设备
        1900,  // UDP - TV - 智能电视用 SSDP/UPnP 发现协议
        5000,  // TCP - Webcam - 网络摄像头 / NAS 控制接口
        5353,  // UDP - Apple - Apple 设备用 Bonjour / mDNS
        9100,  // TCP - PC - PC 打印任务直连端口
        8888,  // TCP - Router - OpenWRT、DD-WRT 路由器 Web UI
        10001, // UDP - Router - Ubiquiti 等网络设备发现服务
        3389,  // TCP - PC - Windows Remote Desktop 服务
        3689,  // TCP - Apple - iTunes 音乐共享（DAAP）
        49152, // TCP - TV - Windows/UPnP 兼容智能电视
        1723,  // TCP - Router - VPN（PPTP）服务路由器端口
        8200   // TCP - TV - DLNA 服务器，常见于电视盒子
    )

    // 检测WIFI是否已连接
    fun isWifiEnabled(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val nc = cm.getNetworkCapabilities(network) ?: return false
        return nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun hasWifiPermission(context: Context): Boolean {
        return AppPermissionHelper.hasPermissions(context, requiredPermissions())
    }

    // 检测WI-FI权限
    fun checkWifiPermission(context: Context, wifiPermissionLauncher: ActivityResultLauncher<Array<String>>) {
        if (!isWifiEnabled(context)) {
            Toast.makeText(context, "Please connect to wifi first", Toast.LENGTH_LONG).show()
            return
        }
        // 权限检查
        val missingPermissions = AppPermissionHelper.missingPermissions(context, requiredPermissions())
        if (missingPermissions.isNotEmpty()) {
            wifiPermissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    // 检测设备类型（端口扫描+推断）
    fun detectDeviceType(dev: Device, localIp: String): WifiDevice {
        var deviceType = "Unknown"
        var deviceName = "Suspected Devices"
        var brandModel = ""
        var riskLevel = 1 // 默认全部可疑

        // 判断本机IP
        if (dev.ip == localIp) {
            riskLevel = 0
            deviceType = "Phone"
            deviceName = "Phone"
        } else {
            // 端口扫描推断类型
            val detectedPorts = Collections.synchronizedList(mutableListOf<Int>())
            val latch = CountDownLatch(1)
            PortScan.onAddress(dev.ip)
                .setPorts(ArrayList(commonPorts))
                .doScan(object : PortScan.PortListener {
                    override fun onResult(portNo: Int, open: Boolean) {
                        if (open) {
                            detectedPorts.add(portNo)
                        }
                    }

                    override fun onFinished(openPorts: java.util.ArrayList<Int>?) {
                        deviceType = analyzeDeviceTypeByPorts(detectedPorts)
                        deviceName = analyzeDeviceNameByPorts(detectedPorts)
                        latch.countDown()
                    }
                })
            latch.await(2500L, TimeUnit.MILLISECONDS)
        }

        if (deviceType.isBlank() || deviceType.equals(
                "Unknown",
                true
            ) || deviceType.equals("Device", true)
        ) {
            deviceType = "Unknown"
        }

        // 可信设备信号强度始终为0
        var signal = 0
        var pingDelay = -1L
        // 只对可疑设备做ping
        if (riskLevel == 1) {
            pingDelay = getPingAndTtl(dev.ip).first
            signal = if (deviceType.equals("Camera", true)) {
                100
            } else {
                estimateSignalStrengthByPing(pingDelay)
            }
        }

        return WifiDevice(
            name = deviceName,
            type = deviceType,
            ip = dev.ip,
            iconRes = R.drawable.svg_icon_sensor,
            signal = signal,
            signalColor = getSignalColor(signal),
            brandModel = brandModel,
            mac = dev.mac ?: "",
            connected = true,
            rssi = signal,
            ping = pingDelay,
            riskLevel = riskLevel
        )
    }

    // 端口类型推断
    private fun analyzeDeviceTypeByPorts(ports: List<Int>): String {
        return when {
            ports.contains(554) || ports.contains(5000) -> "Camera" // RTSP/网络摄像头
            ports.contains(1900) || ports.contains(49152) || ports.contains(8200) -> "TV" // 智能电视/盒子
            ports.contains(22) || ports.contains(139) || ports.contains(445) || ports.contains(3389) || ports.contains(
                9100
            ) -> "PC" // SSH/SMB/远程桌面/打印
            ports.contains(23) || ports.contains(53) || ports.contains(80) || ports.contains(443) || ports.contains(
                8888
            ) || ports.contains(10001) || ports.contains(1723) -> "Router" // 路由器相关
            ports.contains(5353) || ports.contains(3689) -> "Apple" // Apple/Bonjour/iTunes
            else -> "Unknown"
        }
    }

    private fun analyzeDeviceNameByPorts(ports: List<Int>): String {
        return when {
            ports.contains(554) -> "Camera"
            ports.contains(5000) -> "Web Camera"
            ports.contains(1900) || ports.contains(49152) || ports.contains(8200) -> "Smart TV"
            ports.contains(22) || ports.contains(139) || ports.contains(445) || ports.contains(3389) || ports.contains(
                9100
            ) -> "Computer"
            ports.contains(23) || ports.contains(53) || ports.contains(80) || ports.contains(443) || ports.contains(
                8888
            ) || ports.contains(10001) || ports.contains(1723) -> "WiFi Router"
            ports.contains(5353) || ports.contains(3689) -> "Apple Device"
            else -> "Suspected Devices"
        }
    }

    private fun getPingAndTtl(ip: String): Pair<Long, Int> {
        try {
            val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -W 1 $ip")
            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()
            // 解析 time= 和 ttl=
            val timeRegex = Regex("time=([\\d.]+)")
            val ttlRegex = Regex("ttl=(\\d+)")
            val time = timeRegex.find(output)?.groupValues?.get(1)?.toFloatOrNull()?.toLong() ?: -1L
            val ttl = ttlRegex.find(output)?.groupValues?.get(1)?.toIntOrNull() ?: -1
            return Pair(time, ttl)
        } catch (e: Exception) {
            return Pair(-1L, -1)
        }
    }

    private fun estimateSignalStrengthByPing(pingDelay: Long): Int {
        return when {
            pingDelay in 0..100 -> 100
            pingDelay in 101..200 -> 66
            pingDelay > 200 -> 33
            else -> 33
        }
    }

    private fun getSignalColor(signal: Int): Int {
        return when {
            signal >= 60 -> Color.parseColor("#FFF54F36")
            signal >= 40 -> Color.parseColor("#FFF5C836")
            signal >= 20 -> Color.parseColor("#FF05CA67")
            else -> Color.parseColor("#80FFFFFF")
        }
    }
}
