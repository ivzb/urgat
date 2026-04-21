package com.ivzb.urgat.data.trading

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
interface TradingDao {

    @Query("" +
            "SELECT * FROM trading " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<TradingEntity>>

    @RawQuery(observedEntities = [TradingEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<TradingEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<TradingEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "trading",
                searchColumns = listOf("name", "shares", "marketPrice", "fee", "orderType", "currency"),
                dateColumn = "date",
                orderBy = "date DESC, id ASC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

    @Query("SELECT * FROM trading WHERE isDeleted = 0 ORDER BY date DESC, id ASC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TradingEntity>)

    @Upsert
    suspend fun upsert(entity: TradingEntity)

    @Query("SELECT * FROM trading WHERE id = :id")
    suspend fun getById(id: String): TradingEntity?

    @Query("DELETE FROM trading WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT currency FROM trading WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

}
