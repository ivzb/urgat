package com.ivzb.urgat.data.expense

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC LIMIT :limit")
    fun observeAll(limit: Int): Flow<List<ExpenseEntity>>

    @Query("" +
            "SELECT * FROM expenses " +
            "WHERE isDeleted = 0 AND (" +
                "name LIKE '%' || :searchQuery || '%' OR " +
                "amount LIKE '%' || :searchQuery || '%' OR " +
                "category LIKE '%' || :searchQuery || '%'" +
            ")" +
            "ORDER BY date DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int, searchQuery: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY date DESC")
    fun streamAll(): Cursor

    @Upsert
    suspend fun upsert(entity: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getById(id: String): ExpenseEntity?

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
