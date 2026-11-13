package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE dateIso LIKE :monthPrefix || '%' ORDER BY dateIso DESC, createdAtMillis DESC")
    fun observeByMonth(monthPrefix: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
