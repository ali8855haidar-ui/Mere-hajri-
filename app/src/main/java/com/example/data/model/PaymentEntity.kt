package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val amount: Double,
    val dateIso: String,
    val note: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)
