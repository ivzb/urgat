package com.ivzb.urgat.data.weight

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Query("SELECT * FROM weights " +
            "WHERE isDeleted = 0 " +
            "ORDER BY date DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weights WHERE " +
            "isDeleted = 0 AND date >= :from AND date <= :to " +
            "ORDER BY date DESC " +
            "LIMIT :limit")
    fun observeAll(limit: Int, from: Long, to: Long): Flow<List<WeightEntity>>

    @Upsert
    suspend fun upsert(weight: WeightEntity)

    @Query("SELECT * FROM weights WHERE id = :id")
    suspend fun getById(id: String): WeightEntity?

    @Query("DELETE FROM weights WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
