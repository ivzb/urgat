package com.ivzb.urgat.di

import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.source.local.AppDatabase
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.weight.WeightDao
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
    fun provideExpenseDao(appDatabase: AppDatabase): ExpenseDao = appDatabase.expenseDao()

    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao = appDatabase.noteDao()

    @Provides
    fun provideWeightDao(appDatabase: AppDatabase): WeightDao = appDatabase.weightDao()

    @Provides
    fun provideInvestmentDao(appDatabase: AppDatabase): InvestmentDao = appDatabase.investmentDao()

}
