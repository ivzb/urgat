package com.ivzb.urgat.di

import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.saving.SavingDao
import com.ivzb.urgat.data.source.local.AppDatabase
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.watchlist.WatchlistAssetDao
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

    @Provides
    fun provideSavingDao(appDatabase: AppDatabase): SavingDao = appDatabase.savingDao()

    @Provides
    fun provideBudgetsDao(appDatabase: AppDatabase): BudgetDao = appDatabase.budgetDao()

    @Provides
    fun provideWatchlistAssetDao(appDatabase: AppDatabase): WatchlistAssetDao = appDatabase.watchlistAssetDao()

    @Provides
    fun provideFuelConsumptionDao(appDatabase: AppDatabase): FuelConsumptionDao = appDatabase.fuelConsumptionDao()

    @Provides
    fun provideCarMaintenanceDao(appDatabase: AppDatabase): CarMaintenanceDao = appDatabase.carMaintenanceDao()

}
