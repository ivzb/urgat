package com.ivzb.urgat.data.weight

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Query("SELECT * FROM weights " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC " +
            "LIMIT :limit")
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weights WHERE isDeleted = 0 ORDER BY date DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WeightEntity>)

    @Upsert
    suspend fun upsert(entity: WeightEntity)

    @Query("SELECT * FROM weights WHERE id = :id")
    suspend fun getById(id: String): WeightEntity?

    @Query("DELETE FROM weights WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT date FROM weights WHERE isDeleted = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getLatestDate(): Long?

}
