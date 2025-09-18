package com.ivzb.urgat.data.local.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class Expenses(
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface ExpensesDao {
    @Query("SELECT * FROM expenses ORDER BY uid DESC LIMIT 10")
    fun getExpensess(): Flow<List<Expenses>>

    @Insert
    suspend fun insertExpenses(item: Expenses)
}
