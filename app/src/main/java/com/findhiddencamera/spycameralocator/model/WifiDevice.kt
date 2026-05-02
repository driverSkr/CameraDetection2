package com.findhiddencamera.spycameralocator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WifiDevice(
    var name: String = "",
    var type: String = "",
    val ip: String = "",
    var iconRes: Int = 0,
    var signal: Int = 0,             // 信号强度
    var signalColor: Int = 0,        // 信号颜色
    var brandModel: String = "",
    var mac: String = "",
    var connected: Boolean = true,
    var rssi: Int = 0,
    var ping: Long = -1L,
    val riskLevel: Int = 0           // 0 = 安全，1 = 高风险
) : Parcelable
