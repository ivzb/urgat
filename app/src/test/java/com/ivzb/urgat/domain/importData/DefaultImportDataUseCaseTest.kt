package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.budget.BudgetEntity
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceEntity
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.expense.ExpenseEntity
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionEntity
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.investment.InvestmentEntity
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.note.NoteEntity
import com.ivzb.urgat.data.saving.SavingDao
import com.ivzb.urgat.data.saving.SavingEntity
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.todo.TodoEntity
import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.trade.TradeEntity
import com.ivzb.urgat.data.watchlist.WatchlistAssetDao
import com.ivzb.urgat.data.watchlist.WatchlistAssetEntity
import com.ivzb.urgat.data.weight.WeightDao
import com.ivzb.urgat.data.weight.WeightEntity
import com.ivzb.urgat.ui.importData.ImportDataPage
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class DefaultImportDataUseCaseTest {

    private lateinit var importDataRepository: ImportDataRepository
    private lateinit var todoDao: TodoDao
    private lateinit var noteDao: NoteDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var investmentDao: InvestmentDao
    private lateinit var savingDao: SavingDao
    private lateinit var budgetDao: BudgetDao
    private lateinit var watchlistAssetDao: WatchlistAssetDao
    private lateinit var weightDao: WeightDao
    private lateinit var fuelConsumptionDao: FuelConsumptionDao
    private lateinit var carMaintenanceDao: CarMaintenanceDao
    private lateinit var tradeDao: TradeDao
    private val dispatcher = StandardTestDispatcher()

    private lateinit var importDataUseCase: ImportDataUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        importDataRepository = mockk(relaxed = true)
        todoDao = mockk(relaxed = true)
        noteDao = mockk(relaxed = true)
        expenseDao = mockk(relaxed = true)
        investmentDao = mockk(relaxed = true)
        savingDao = mockk(relaxed = true)
        budgetDao = mockk(relaxed = true)
        watchlistAssetDao = mockk(relaxed = true)
        weightDao = mockk(relaxed = true)
        fuelConsumptionDao = mockk(relaxed = true)
        carMaintenanceDao = mockk(relaxed = true)
        tradeDao = mockk(relaxed = true)

        importDataUseCase = DefaultImportDataUseCase(
            DefaultImportTodosUseCase(importDataRepository, todoDao),
            DefaultImportNotesUseCase(importDataRepository, noteDao),
            DefaultImportExpensesUseCase(importDataRepository, expenseDao),
            DefaultImportInvestmentsUseCase(importDataRepository, investmentDao),
            DefaultImportSavingsUseCase(importDataRepository, savingDao),
            DefaultImportBudgetsUseCase(importDataRepository, budgetDao),
            DefaultImportWatchlistAssetsUseCase(importDataRepository, watchlistAssetDao),
            DefaultImportWeightsUseCase(importDataRepository, weightDao),
            DefaultImportFuelConsumptionUseCase(importDataRepository, fuelConsumptionDao),
            DefaultImportCarMaintenanceUseCase(importDataRepository, carMaintenanceDao),
            DefaultImportTradesUseCase(importDataRepository, tradeDao),
            dispatcher,
        )
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val uri = mockk<Uri>()

        importDataUseCase(uri)

        coVerify(exactly = 1) {
            importDataRepository.importCsv<TodoEntity>(eq(uri), eq("todos.csv"), any(), any())
            importDataRepository.importCsv<NoteEntity>(eq(uri), eq("notes.csv"), any(), any())
            importDataRepository.importCsv<ExpenseEntity>(eq(uri), eq("expenses.csv"), any(), any())
            importDataRepository.importCsv<InvestmentEntity>(eq(uri), eq("investments.csv"), any(), any())
            importDataRepository.importCsv<SavingEntity>(eq(uri), eq("savings.csv"), any(), any())
            importDataRepository.importCsv<BudgetEntity>(eq(uri), eq("budgets.csv"), any(), any())
            importDataRepository.importCsv<WatchlistAssetEntity>(eq(uri), eq("watchlist_assets.csv"), any(), any())
            importDataRepository.importCsv<WeightEntity>(eq(uri), eq("weights.csv"), any(), any())
            importDataRepository.importCsv<FuelConsumptionEntity>(eq(uri), eq("fuel_consumption.csv"), any(), any())
            importDataRepository.importCsv<CarMaintenanceEntity>(eq(uri), eq("car_maintenance.csv"), any(), any())
            importDataRepository.importCsv<TradeEntity>(eq(uri), eq("trades.csv"), any(), any())
        }
    }

    @Test
    fun `importManualData should import content with the use case of the page`() = runTest {
        val importTodosUseCase = mockk<ImportTodosUseCase>(relaxed = true)
        val importNotesUseCase = mockk<ImportNotesUseCase>(relaxed = true)
        val importExpensesUseCase = mockk<ImportExpensesUseCase>(relaxed = true)
        val importInvestmentsUseCase = mockk<ImportInvestmentsUseCase>(relaxed = true)
        val importSavingsUseCase = mockk<ImportSavingsUseCase>(relaxed = true)
        val importBudgetsUseCase = mockk<ImportBudgetsUseCase>(relaxed = true)
        val importWatchlistAssetsUseCase = mockk<ImportWatchlistAssetsUseCase>(relaxed = true)
        val importWeightsUseCase = mockk<ImportWeightsUseCase>(relaxed = true)
        val importFuelConsumptionUseCase = mockk<ImportFuelConsumptionUseCase>(relaxed = true)
        val importCarMaintenanceUseCase = mockk<ImportCarMaintenanceUseCase>(relaxed = true)
        val importTradesUseCase = mockk<ImportTradesUseCase>(relaxed = true)
        val useCase = DefaultImportDataUseCase(
            importTodosUseCase,
            importNotesUseCase,
            importExpensesUseCase,
            importInvestmentsUseCase,
            importSavingsUseCase,
            importBudgetsUseCase,
            importWatchlistAssetsUseCase,
            importWeightsUseCase,
            importFuelConsumptionUseCase,
            importCarMaintenanceUseCase,
            importTradesUseCase,
            dispatcher,
        )

        useCase.importManualData(ImportDataPage.Todos, "Todos")
        useCase.importManualData(ImportDataPage.Notes, "Notes")
        useCase.importManualData(ImportDataPage.Expenses, "Expenses")
        useCase.importManualData(ImportDataPage.Investments, "Investments")
        useCase.importManualData(ImportDataPage.Savings, "Savings")
        useCase.importManualData(ImportDataPage.Budgets, "Budgets")
        useCase.importManualData(ImportDataPage.WatchlistAssets, "WatchlistAssets")
        useCase.importManualData(ImportDataPage.Weights, "Weights")
        useCase.importManualData(ImportDataPage.FuelConsumption, "FuelConsumption")
        useCase.importManualData(ImportDataPage.CarMaintenance, "CarMaintenance")
        useCase.importManualData(ImportDataPage.Trades, "Trades")

        coVerify(exactly = 1) {
            importTodosUseCase.importTodos("Todos")
            importNotesUseCase.importNotes("Notes")
            importExpensesUseCase.importExpenses("Expenses")
            importInvestmentsUseCase.importInvestments("Investments")
            importSavingsUseCase.importSavings("Savings")
            importBudgetsUseCase.importBudgets("Budgets")
            importWatchlistAssetsUseCase.importWatchlistAssets("WatchlistAssets")
            importWeightsUseCase.importWeights("Weights")
            importFuelConsumptionUseCase.importFuelConsumption("FuelConsumption")
            importCarMaintenanceUseCase.importCarMaintenance("CarMaintenance")
            importTradesUseCase.importTrades("Trades")
        }
    }
}
