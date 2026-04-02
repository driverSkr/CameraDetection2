package com.findhiddencamera.spycameralocator.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.findhiddencamera.spycameralocator.model.DetectBluetoothDevice
import com.findhiddencamera.spycameralocator.model.DetectWifiDevice
import com.findhiddencamera.spycameralocator.room.converter.Converters
import com.findhiddencamera.spycameralocator.room.dao.BluetoothDao
import com.findhiddencamera.spycameralocator.room.dao.WifiDao

@Database(entities = [DetectWifiDevice::class, DetectBluetoothDevice::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)  // 所有实体共享同一个转换器配置
abstract class DetectDataBase : RoomDatabase() {

    abstract fun getWifiDao(): WifiDao
    abstract fun getBluetoothDao(): BluetoothDao

    companion object {
        @Volatile
        private var instance: DetectDataBase? = null
        private val Lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(Lock) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) = Room
            .databaseBuilder(context.applicationContext, DetectDataBase::class.java, "detect_database.db")
            .build()
    }
}