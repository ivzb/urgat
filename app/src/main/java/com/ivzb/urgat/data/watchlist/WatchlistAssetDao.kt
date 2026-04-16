package com.ivzb.urgat.data.watchlist

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
interface WatchlistAssetDao {

    @Query("" +
            "SELECT * FROM watchlist_assets " +
            "WHERE isDeleted = 0 AND createdAt BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY createdAt DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<WatchlistAssetEntity>>

    @RawQuery(observedEntities = [WatchlistAssetEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<WatchlistAssetEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<WatchlistAssetEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "watchlist_assets",
                searchColumns = listOf("name"),
                dateColumn = "createdAt",
                orderBy = "createdAt DESC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

    @Query("SELECT * FROM watchlist_assets WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WatchlistAssetEntity>)

    @Upsert
    suspend fun upsert(entity: WatchlistAssetEntity)

    @Query("SELECT * FROM watchlist_assets WHERE id = :id")
    suspend fun getById(id: String): WatchlistAssetEntity?

    @Query("DELETE FROM watchlist_assets WHERE id = :id")
    suspend fun deleteById(id: String): Int

}