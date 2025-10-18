package com.ivzb.urgat.data.expense

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

}
