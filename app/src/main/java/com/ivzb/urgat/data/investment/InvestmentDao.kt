package com.ivzb.urgat.data.investment

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
interface InvestmentDao {

    @Query("" +
            "SELECT * FROM investments " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<InvestmentEntity>>

    @RawQuery(observedEntities = [InvestmentEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<InvestmentEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<InvestmentEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "investments",
                searchColumns = listOf("name", "shares", "category", "currency"),
                dateColumn = "date",
                orderBy = "date DESC, id ASC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

    @Query("SELECT * FROM investments WHERE isDeleted = 0 ORDER BY date DESC, id ASC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InvestmentEntity>)

    @Upsert
    suspend fun upsert(entity: InvestmentEntity)

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: String): InvestmentEntity?

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT currency FROM investments WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

}
