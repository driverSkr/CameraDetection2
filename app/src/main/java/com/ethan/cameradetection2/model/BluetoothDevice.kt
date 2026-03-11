package com.ethan.cameradetection2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BluetoothDevice(
    var name: String,
    var type: String,
    val mac: String,
    var iconRes: Int,
    val signal: Int, // 信号强度
    val signalColor: Int, // 信号颜色
    val uuid: String = "",
    val connected: Boolean = true,
    val rssi: Int = 0,
    val riskLevel: Int = 0 // 0=安全，1=高风险
) : Parcelable