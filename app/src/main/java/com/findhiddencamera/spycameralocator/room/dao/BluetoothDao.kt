package com.findhiddencamera.spycameralocator.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.findhiddencamera.spycameralocator.model.DetectBluetoothDevice

@Dao
interface BluetoothDao {

    @Delete
    suspend fun delete(device: DetectBluetoothDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevices(devices: List<DetectBluetoothDevice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevice(device: DetectBluetoothDevice): Long  // 返回自增ID

    @Query("SELECT * FROM DetectBluetoothDevice ORDER BY createTime DESC")
    suspend fun getAllDevice(): MutableList<DetectBluetoothDevice>

    @Query("DELETE FROM DetectBluetoothDevice WHERE createTime < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT * FROM DetectBluetoothDevice WHERE createTime >= :startTime ORDER BY createTime DESC")
    suspend fun getSince(startTime: Long): List<DetectBluetoothDevice>
}