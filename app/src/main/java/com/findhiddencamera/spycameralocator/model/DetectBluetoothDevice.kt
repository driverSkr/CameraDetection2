package com.findhiddencamera.spycameralocator.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class DetectBluetoothDevice(
    @PrimaryKey(autoGenerate = true)  // 改为自增主键
    var id: Long = 0L,  // 新增自增主键
    var createTime: Long = System.currentTimeMillis().div(1000),
    var suspiciousDevices: List<BluetoothDevice>,
    var trustedDevices: List<BluetoothDevice>
) : Parcelable
