package com.ethan.cameradetection2.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ethan.cameradetection2.model.DetectWifiDevice

@Dao
interface WifiDao {
    @Delete
    suspend fun delete(device: DetectWifiDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevices(devices: List<DetectWifiDevice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevice(device: DetectWifiDevice): Long  // 返回自增ID

    @Query("SELECT * FROM DetectWifiDevice ORDER BY createTime DESC")
    suspend fun getAllDevice(): MutableList<DetectWifiDevice>

    @Query("DELETE FROM DetectWifiDevice WHERE createTime < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT * FROM DetectWifiDevice WHERE createTime >= :startTime ORDER BY createTime DESC")
    suspend fun getSince(startTime: Long): List<DetectWifiDevice>
}