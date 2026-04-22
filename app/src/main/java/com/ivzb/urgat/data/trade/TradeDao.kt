package com.ivzb.urgat.data.trade

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
interface TradeDao {

    @Query("" +
            "SELECT * FROM trade " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<TradeEntity>>

    @RawQuery(observedEntities = [TradeEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<TradeEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<TradeEntity>> {
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

    @Query("SELECT * FROM trade WHERE isDeleted = 0 ORDER BY date DESC, id ASC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TradeEntity>)

    @Upsert
    suspend fun upsert(entity: TradeEntity)

    @Query("SELECT * FROM trade WHERE id = :id")
    suspend fun getById(id: String): TradeEntity?

    @Query("DELETE FROM trade WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT currency FROM trade WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

}
