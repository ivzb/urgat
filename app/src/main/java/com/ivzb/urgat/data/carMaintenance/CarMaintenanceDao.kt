package com.ivzb.urgat.data.carMaintenance

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
interface CarMaintenanceDao {

    @Query("SELECT * FROM car_maintenance " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC " +
            "LIMIT :limit")
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<CarMaintenanceEntity>>

    @RawQuery(observedEntities = [CarMaintenanceEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<CarMaintenanceEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<CarMaintenanceEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "car_maintenance",
                searchColumns = listOf("description", "partNumber", "carName", "odometer"),
                dateColumn = "date",
                orderBy = "date DESC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

    @Query("SELECT * FROM car_maintenance WHERE isDeleted = 0 ORDER BY date DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CarMaintenanceEntity>)

    @Upsert
    suspend fun upsert(entity: CarMaintenanceEntity)

    @Query("SELECT * FROM car_maintenance WHERE id = :id")
    suspend fun getById(id: String): CarMaintenanceEntity?

    @Query("DELETE FROM car_maintenance WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT date FROM car_maintenance WHERE isDeleted = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getLatestDate(): Long?

    @Query("SELECT carName FROM car_maintenance WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCarName(): String?

}

