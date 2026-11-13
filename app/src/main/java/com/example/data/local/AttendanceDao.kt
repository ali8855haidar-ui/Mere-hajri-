package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE dateIso LIKE :monthPrefix || '%' ORDER BY dateIso DESC")
    fun observeByMonth(monthPrefix: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_records ORDER BY dateIso DESC")
    fun observeAll(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_records WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getByDate(dateIso: String): AttendanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: AttendanceEntity)

    @Query("DELETE FROM attendance_records WHERE dateIso = :dateIso")
    suspend fun deleteByDate(dateIso: String)
}
