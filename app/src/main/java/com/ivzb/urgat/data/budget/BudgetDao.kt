package com.ivzb.urgat.data.budget

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("" +
            "SELECT * FROM budgets " +
            "WHERE isDeleted = 0 " +
            "ORDER BY date DESC " +
            "LIMIT :limit"
    )
    fun observeAll(limit: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE isDeleted = 0 ORDER BY date DESC")
    fun stream(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BudgetEntity>)

    @Upsert
    suspend fun upsert(entity: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getById(id: String): BudgetEntity?

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteById(id: String): Int

}
