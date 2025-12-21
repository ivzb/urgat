package com.ivzb.urgat.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.expense.ExpenseEntity
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.investment.InvestmentEntity
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.note.NoteEntity
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.todo.TodoEntity
import com.ivzb.urgat.data.weight.WeightDao
import com.ivzb.urgat.data.weight.WeightEntity

@Database(entities = [
    TodoEntity::class,
    ExpenseEntity::class,
    NoteEntity::class,
    WeightEntity::class,
    InvestmentEntity::class,
 ], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    abstract fun expenseDao(): ExpenseDao

    abstract fun noteDao(): NoteDao

    abstract fun weightDao(): WeightDao

    abstract fun investmentDao(): InvestmentDao

}