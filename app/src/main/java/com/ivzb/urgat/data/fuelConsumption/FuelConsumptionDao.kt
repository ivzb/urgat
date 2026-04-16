package com.ivzb.urgat.data.fuelConsumption

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.ivzb.urgat.data.base.SearchQueryBuilder
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelConsumptionDao {

    @Query("SELECT * FROM fuel_consumptions " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC " +
            "LIMIT :limit")
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<FuelConsumptionEntity>>

    @RawQuery(observedEntities = [FuelConsumptionEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<FuelConsumptionEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<FuelConsumptionEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "fuel_consumptions",
                searchColumns = listOf("amount", "price", "currency", "type", "carName", "odometer"),
                dateColumn = "date",
                orderBy = "date DESC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

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

    @Query("SELECT currency FROM fuel_consumptions WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

    @Query("SELECT type FROM fuel_consumptions WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedType(): String?

    @Query("SELECT carName FROM fuel_consumptions WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCarName(): String?

}

