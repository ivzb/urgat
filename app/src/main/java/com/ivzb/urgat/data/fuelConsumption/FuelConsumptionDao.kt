package com.ivzb.urgat.data.fuelConsumption

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelConsumptionDao {

    @Query("SELECT * FROM fuel_consumptions " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC " +
            "LIMIT :limit")
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<FuelConsumptionEntity>>

    @Query("SELECT * FROM fuel_consumptions " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd AND (" +
                "amount LIKE '%' || :searchQuery || '%' OR " +
                "price LIKE '%' || :searchQuery || '%' OR " +
                "currency LIKE '%' || :searchQuery || '%' OR " +
                "carName LIKE '%' || :searchQuery || '%' OR " +
                "odometer LIKE '%' || :searchQuery || '%'" +
            ")" +
            "ORDER BY date DESC " +
            "LIMIT :limit")
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<FuelConsumptionEntity>>

    @Query("SELECT * FROM fuel_consumptions WHERE isDeleted = 0 ORDER BY date DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FuelConsumptionEntity>)

    @Upsert
    suspend fun upsert(entity: FuelConsumptionEntity)

    @Query("SELECT * FROM fuel_consumptions WHERE id = :id")
    suspend fun getById(id: String): FuelConsumptionEntity?

    @Query("DELETE FROM fuel_consumptions WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT date FROM fuel_consumptions WHERE isDeleted = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getLatestDate(): Long?

}

