package com.ivzb.urgat.data.saving

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
interface SavingDao {

    @Query("" +
            "SELECT * FROM savings " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<SavingEntity>>

    @RawQuery(observedEntities = [SavingEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<SavingEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<SavingEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "savings",
                searchColumns = listOf("name", "amount"),
                dateColumn = "date",
                orderBy = "date DESC, id ASC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

    @Query("SELECT * FROM savings WHERE isDeleted = 0 ORDER BY date DESC, id ASC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SavingEntity>)

    @Upsert
    suspend fun upsert(entity: SavingEntity)

    @Query("SELECT * FROM savings WHERE id = :id")
    suspend fun getById(id: String): SavingEntity?

    @Query("DELETE FROM savings WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT currency FROM savings WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

}
