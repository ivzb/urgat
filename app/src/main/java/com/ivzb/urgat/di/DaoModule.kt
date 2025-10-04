package com.ivzb.urgat.di

import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.source.local.AppDatabase
import com.ivzb.urgat.data.todo.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao = database.todoDao()

    @Provides
    fun provideExpensesDao(appDatabase: AppDatabase): ExpenseDao = appDatabase.expenseDao()

}
