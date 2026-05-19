package com.findhiddencamera.spycameralocator.utils

object DetectionSessionHelper {
    var hasWifiDetected: Boolean = false
        private set

    var hasBluetoothDetected: Boolean = false
        private set

    fun markWifiDetected() {
        hasWifiDetected = true
    }

    fun markBluetoothDetected() {
        hasBluetoothDetected = true
    }
}
