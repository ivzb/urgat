package com.ivzb.urgat.domain.exportData

import android.database.Cursor
import android.net.Uri
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.csv.CsvMapping.Companion.BUDGETS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.CAR_MAINTENANCE_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TRADES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.FUEL_CONSUMPTION_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.SAVINGS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WATCHLIST_ASSETS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_MAPPING
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.exportData.ExportDataRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.saving.SavingDao
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.trade.TradeDao
import com.ivzb.urgat.data.watchlist.WatchlistAssetDao
import com.ivzb.urgat.data.weight.WeightDao
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class DefaultExportDataUseCaseTest {

    private lateinit var exportDataRepository: ExportDataRepository
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

    private lateinit var exportDataUseCase: ExportDataUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        exportDataRepository = mockk(relaxed = true)
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

        exportDataUseCase = DefaultExportDataUseCase(
            exportDataRepository,
            todoDao,
            noteDao,
            expenseDao,
            investmentDao,
            savingDao,
            budgetDao,
            watchlistAssetDao,
            weightDao,
            fuelConsumptionDao,
            carMaintenanceDao,
            tradeDao,
            dispatcher,
        )
    }

    @Test
    fun `invoke should call repository create with correct parameters`() = runTest {
        val uri = mockk<Uri>()

        val todosCursor = mockk<Cursor>()
        every { todoDao.stream() } returns todosCursor

        val notesCursor = mockk<Cursor>()
        every { noteDao.stream() } returns notesCursor

        val expensesCursor = mockk<Cursor>()
        every { expenseDao.stream() } returns expensesCursor

        val investmentsCursor = mockk<Cursor>()
        every { investmentDao.stream() } returns investmentsCursor

        val savingsCursor = mockk<Cursor>()
        every { savingDao.stream() } returns savingsCursor

        val budgetsCursor = mockk<Cursor>()
        every { budgetDao.stream() } returns budgetsCursor

        val watchlistAssetsCursor = mockk<Cursor>()
        every { watchlistAssetDao.stream() } returns watchlistAssetsCursor

        val weightsCursor = mockk<Cursor>()
        every { weightDao.stream() } returns weightsCursor

        val fuelConsumptionCursor = mockk<Cursor>()
        every { fuelConsumptionDao.stream() } returns fuelConsumptionCursor

        val carMaintenanceCursor = mockk<Cursor>()
        every { carMaintenanceDao.stream() } returns carMaintenanceCursor

        val tradesCursor = mockk<Cursor>()
        every { tradeDao.stream() } returns tradesCursor

        exportDataUseCase(uri)

        coVerify(exactly = 1) {
            exportDataRepository.exportCsv(eq(uri), eq(todosCursor), eq("todos.csv"), eq(TODOS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(notesCursor), eq("notes.csv"), eq(NOTES_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(expensesCursor), eq("expenses.csv"), eq(EXPENSES_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(investmentsCursor), eq("investments.csv"), eq(INVESTMENTS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(savingsCursor), eq("savings.csv"), eq(SAVINGS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(budgetsCursor), eq("budgets.csv"), eq(BUDGETS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(watchlistAssetsCursor), eq("watchlist_assets.csv"), eq(WATCHLIST_ASSETS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(weightsCursor), eq("weights.csv"), eq(WEIGHTS_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(fuelConsumptionCursor), eq("fuel_consumption.csv"), eq(FUEL_CONSUMPTION_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(carMaintenanceCursor), eq("car_maintenance.csv"), eq(CAR_MAINTENANCE_CSV_MAPPING))
            exportDataRepository.exportCsv(eq(uri), eq(tradesCursor), eq("trades.csv"), eq(TRADES_CSV_MAPPING))
        }
    }
}
