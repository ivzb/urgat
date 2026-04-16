package com.ivzb.urgat.data.expense

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
interface ExpenseDao {

    @Query("" +
            "SELECT * FROM expenses " +
            "WHERE isDeleted = 0 AND date BETWEEN :dateStart AND :dateEnd " +
            "ORDER BY date DESC, id ASC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long): Flow<List<ExpenseEntity>>

    @RawQuery(observedEntities = [ExpenseEntity::class])
    fun observeAllRaw(query: SupportSQLiteQuery): Flow<List<ExpenseEntity>>

    fun observeAll(limit: Int, dateStart: Long, dateEnd: Long, searchQuery: String): Flow<List<ExpenseEntity>> {
        return observeAllRaw(
            SearchQueryBuilder.build(
                tableName = "expenses",
                searchColumns = listOf("name", "amount", "category"),
                dateColumn = "date",
                orderBy = "date DESC, id ASC",
                limit = limit,
                dateStart = dateStart,
                dateEnd = dateEnd,
                searchQuery = searchQuery,
            )
        )
    }

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC, id ASC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ExpenseEntity>)

    @Upsert
    suspend fun upsert(entity: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: String): ExpenseEntity?

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT currency FROM expenses WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastUsedCurrency(): String?

}
