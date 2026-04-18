package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.csv.CsvMapping.Companion.BUDGETS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.FUEL_CONSUMPTION_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.CAR_MAINTENANCE_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.SAVINGS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WATCHLIST_ASSETS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseBudgetCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseExpenseCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseFuelConsumptionCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseCarMaintenanceCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseInvestmentCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseNoteCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseSavingCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseTodoCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseWatchlistAssetCsv
import com.ivzb.urgat.data.csv.CsvMapping.Companion.parseWeightCsv
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
import com.ivzb.urgat.data.importData.ImportDataRepository
import com.ivzb.urgat.data.investment.InvestmentDao
import com.ivzb.urgat.data.note.NoteDao
import com.ivzb.urgat.data.saving.SavingDao
import com.ivzb.urgat.data.todo.TodoDao
import com.ivzb.urgat.data.watchlist.WatchlistAssetDao
import com.ivzb.urgat.data.weight.WeightDao
import com.ivzb.urgat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface ImportDataUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importExpenses(content: String)

    suspend fun importInvestments(content: String)

    suspend fun importSavings(content: String)

    suspend fun importBudgets(content: String)

    suspend fun importTodos(content: String)

    suspend fun importNotes(content: String)

    suspend fun importWatchlistAssets(content: String)

    suspend fun importWeights(content: String)

    suspend fun importFuelConsumption(content: String)

    suspend fun importCarMaintenance(content: String)

}

class DefaultImportDataUseCase @Inject constructor(
    private val importDataRepository: ImportDataRepository,
    private val todoDao: TodoDao,
    private val noteDao: NoteDao,
    private val expenseDao: ExpenseDao,
    private val investmentDao: InvestmentDao,
    private val savingDao: SavingDao,
    private val budgetDao: BudgetDao,
    private val watchlistAssetDao: WatchlistAssetDao,
    private val weightDao: WeightDao,
    private val fuelConsumptionDao: FuelConsumptionDao,
    private val carMaintenanceDao: CarMaintenanceDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): ImportDataUseCase {

    override suspend operator fun invoke(uri: Uri) {
        return withContext(dispatcher) {
            importDataRepository.importCsv(uri, TODOS_CSV_FILE_NAME, ::parseTodoCsv, todoDao::insertAll)
            importDataRepository.importCsv(uri, NOTES_CSV_FILE_NAME, ::parseNoteCsv, noteDao::insertAll)
            importDataRepository.importCsv(uri, EXPENSES_CSV_FILE_NAME, ::parseExpenseCsv, expenseDao::insertAll)
            importDataRepository.importCsv(uri, INVESTMENTS_CSV_FILE_NAME, ::parseInvestmentCsv, investmentDao::insertAll)
            importDataRepository.importCsv(uri, SAVINGS_CSV_FILE_NAME, ::parseSavingCsv, savingDao::insertAll)
            importDataRepository.importCsv(uri, BUDGETS_CSV_FILE_NAME, ::parseBudgetCsv, budgetDao::insertAll)
            importDataRepository.importCsv(uri, WATCHLIST_ASSETS_CSV_FILE_NAME, ::parseWatchlistAssetCsv, watchlistAssetDao::insertAll)
            importDataRepository.importCsv(uri, WEIGHTS_CSV_FILE_NAME, ::parseWeightCsv, weightDao::insertAll)
            importDataRepository.importCsv(uri, FUEL_CONSUMPTION_CSV_FILE_NAME, ::parseFuelConsumptionCsv, fuelConsumptionDao::insertAll)
            importDataRepository.importCsv(uri, CAR_MAINTENANCE_CSV_FILE_NAME, ::parseCarMaintenanceCsv, carMaintenanceDao::insertAll)
        }
    }

    override suspend fun importExpenses(content: String) {
        importDataRepository.importString(content, ::parseExpenseCsv, expenseDao::insertAll)
    }

    override suspend fun importInvestments(content: String) {
        importDataRepository.importString(content, ::parseInvestmentCsv, investmentDao::insertAll)
    }

    override suspend fun importSavings(content: String) {
        importDataRepository.importString(content, ::parseSavingCsv, savingDao::insertAll)
    }

    override suspend fun importBudgets(content: String) {
        importDataRepository.importString(content, ::parseBudgetCsv, budgetDao::insertAll)
    }

    override suspend fun importTodos(content: String) {
        importDataRepository.importString(content, ::parseTodoCsv, todoDao::insertAll)
    }

    override suspend fun importNotes(content: String) {
        importDataRepository.importString(content, ::parseNoteCsv, noteDao::insertAll)
    }

    override suspend fun importWatchlistAssets(content: String) {
        importDataRepository.importString(content, ::parseWatchlistAssetCsv, watchlistAssetDao::insertAll)
    }

    override suspend fun importWeights(content: String) {
        importDataRepository.importString(content, ::parseWeightCsv, weightDao::insertAll)
    }

    override suspend fun importFuelConsumption(content: String) {
        importDataRepository.importString(content, ::parseFuelConsumptionCsv, fuelConsumptionDao::insertAll)
    }

    override suspend fun importCarMaintenance(content: String) {
        importDataRepository.importString(content, ::parseCarMaintenanceCsv, carMaintenanceDao::insertAll)
    }

}
