package com.ivzb.urgat.data.expense

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class Expense(
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense ORDER BY uid DESC LIMIT 10")
    fun getExpenses(): Flow<List<Expense>>

    @Insert
    suspend fun insertExpense(item: Expense)
}
