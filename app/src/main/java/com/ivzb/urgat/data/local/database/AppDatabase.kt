package com.ivzb.urgat.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Expenses::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expensesDao(): ExpensesDao
}
