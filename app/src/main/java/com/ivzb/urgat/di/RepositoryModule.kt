package com.ivzb.urgat.di

import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.data.budget.DefaultBudgetsRepository
import com.ivzb.urgat.data.car.CarRepository
import com.ivzb.urgat.data.car.DefaultCarRepository
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceRepository
import com.ivzb.urgat.data.carMaintenance.DefaultCarMaintenanceRepository
import com.ivzb.urgat.data.expense.DefaultExpensesRepository
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.data.exportData.DefaultExportDataRepository
import com.ivzb.urgat.data.exportData.ExportDataRepository
import com.ivzb.urgat.data.finance.DefaultFinanceRepository
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.data.fire.DefaultFireRepository
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.data.fuelConsumption.DefaultFuelConsumptionRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionRepository
import com.ivzb.urgat.data.importData.DefaultImportDataRepository
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.investment.DefaultInvestmentsRepository
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.library.DefaultLibraryRepository
import com.ivzb.urgat.data.library.LibraryRepository
import com.ivzb.urgat.data.note.DefaultNotesRepository
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.data.random.DefaultRandomRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.saving.DefaultSavingsRepository
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.data.todo.DefaultTodoRepository
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.data.trade.DefaultTradesRepository
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.data.watchlist.DefaultWatchlistAssetsRepository
import com.ivzb.urgat.data.watchlist.WatchlistAssetsRepository
import com.ivzb.urgat.data.weight.DefaultWeightsRepository
import com.ivzb.urgat.data.weight.WeightsRepository
import com.ivzb.urgat.data.forex.DefaultForexRepository
import com.ivzb.urgat.data.forex.ForexRepository
import com.ivzb.urgat.data.investmentMarketPrice.DefaultInvestmentMarketMarketPriceRepository
import com.ivzb.urgat.data.investmentMarketPrice.InvestmentMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPriceRepository
import com.ivzb.urgat.data.marketPrice.YahooFinanceMarketPriceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindTodoRepository(todoRepository: DefaultTodoRepository): TodoRepository

    @Singleton
    @Binds
    fun bindsExpensesRepository(expensesRepository: DefaultExpensesRepository): ExpensesRepository

    @Singleton
    @Binds
    fun bindsNotesRepository(notesRepository: DefaultNotesRepository): NotesRepository

    @Singleton
    @Binds
    fun bindsWeightsRepository(weightsRepository: DefaultWeightsRepository): WeightsRepository

    @Singleton
    @Binds
    fun bindsInvestmentsRepository(investmentsRepository: DefaultInvestmentsRepository): InvestmentsRepository

    @Singleton
    @Binds
    fun bindsExportDataRepository(exportDataRepository: DefaultExportDataRepository): ExportDataRepository

    @Singleton
    @Binds
    fun bindsImportDataRepository(importDataRepository: DefaultImportDataRepository): ImportDataRepository

    @Singleton
    @Binds
    fun bindsRandomRepository(randomRepository: DefaultRandomRepository): RandomRepository

    @Singleton
    @Binds
    fun bindsSavingsRepository(savingsRepository: DefaultSavingsRepository): SavingsRepository

    @Singleton
    @Binds
    fun bindsBudgetsRepository(budgetsRepository: DefaultBudgetsRepository): BudgetsRepository

    @Singleton
    @Binds
    fun bindsFireRepository(fireRepository: DefaultFireRepository): FireRepository

    @Singleton
    @Binds
    fun bindsFinanceRepository(financeRepository: DefaultFinanceRepository): FinanceRepository

    @Singleton
    @Binds
    fun bindsLibraryRepository(libraryRepository: DefaultLibraryRepository): LibraryRepository

    @Singleton
    @Binds
    fun bindsCarRepository(carRepository: DefaultCarRepository): CarRepository

    @Singleton
    @Binds
    fun bindsMarketPriceRepository(marketPriceRepository: YahooFinanceMarketPriceRepository): MarketPriceRepository

    @Singleton
    @Binds
    fun bindsForexRepository(forexRepository: DefaultForexRepository): ForexRepository

    @Singleton
    @Binds
    fun bindsInvestmentPriceRepository(investmentPriceRepository: DefaultInvestmentMarketMarketPriceRepository): InvestmentMarketPriceRepository

    @Singleton
    @Binds
    fun bindsWatchlistAssetsRepository(watchlistAssetsRepository: DefaultWatchlistAssetsRepository): WatchlistAssetsRepository

    @Singleton
    @Binds
    fun bindsFuelConsumptionRepository(fuelConsumptionRepository: DefaultFuelConsumptionRepository): FuelConsumptionRepository

    @Singleton
    @Binds
    fun bindsCarMaintenanceRepository(carMaintenanceRepository: DefaultCarMaintenanceRepository): CarMaintenanceRepository

    @Singleton
    @Binds
    fun bindsTradesRepository(tradesRepository: DefaultTradesRepository): TradesRepository

}
