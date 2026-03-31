package com.ethan.cameradetection2.room.converter

import androidx.room.TypeConverter
import com.ethan.cameradetection2.model.BluetoothDevice
import com.ethan.cameradetection2.model.WifiDevice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // 为WifiDevice提供的转换器
    @TypeConverter
    fun fromWifiDeviceList(value: List<WifiDevice>?): String {
        return if (value == null) {
            "[]"
        } else {
            gson.toJson(value)
        }
    }

    @TypeConverter
    fun toWifiDeviceList(value: String): List<WifiDevice> {
        if (value.isEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<List<WifiDevice>>() {}.type
        return gson.fromJson(value, type)
    }

    // 为BluetoothDevice提供的转换器
    @TypeConverter
    fun fromBluetoothDeviceList(value: List<BluetoothDevice>?): String {
        return if (value == null) {
            "[]"
        } else {
            gson.toJson(value)
        }
    }

    @TypeConverter
    fun toBluetoothDeviceList(value: String): List<BluetoothDevice> {
        if (value.isEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<List<BluetoothDevice>>() {}.type
        return gson.fromJson(value, type)
    }
}