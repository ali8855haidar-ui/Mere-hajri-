package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceEntity(
    @PrimaryKey
    val dateIso: String,
    val status: String,
    val dutyHours: Double,
    val otHours: Double,
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    fun statusEnum(): AttendanceStatus = AttendanceStatus.fromStorageKey(status)
}
