package com.ivzb.urgat.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.todo.LocalTodo
import com.ivzb.urgat.data.todo.TodoDao

@Database(entities = [LocalTodo::class, Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    abstract fun expenseDao(): ExpenseDao
}