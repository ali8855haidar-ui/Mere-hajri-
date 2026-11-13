package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AttendanceEntity
import com.example.data.model.PaymentEntity

@Database(
    entities = [AttendanceEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MeriHajiriDatabase : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: MeriHajiriDatabase? = null

        fun getInstance(context: Context): MeriHajiriDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MeriHajiriDatabase::class.java,
                    "merihajiri_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
