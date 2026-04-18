package com.ivzb.urgat.domain.exportData

import android.net.Uri
import com.ivzb.urgat.data.budget.BudgetDao
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceDao
import com.ivzb.urgat.data.csv.CsvMapping.Companion.BUDGETS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.BUDGETS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.CAR_MAINTENANCE_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.CAR_MAINTENANCE_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.EXPENSES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.FUEL_CONSUMPTION_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.FUEL_CONSUMPTION_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.INVESTMENTS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.NOTES_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.SAVINGS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.SAVINGS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.TODOS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WATCHLIST_ASSETS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WATCHLIST_ASSETS_CSV_MAPPING
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_FILE_NAME
import com.ivzb.urgat.data.csv.CsvMapping.Companion.WEIGHTS_CSV_MAPPING
import com.ivzb.urgat.data.expense.ExpenseDao
import com.ivzb.urgat.data.exportData.ExportDataRepository
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionDao
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

interface ExportDataUseCase {

    suspend operator fun invoke(uri: Uri)

}

class DefaultExportDataUseCase @Inject constructor(
    private val exportDataRepository: ExportDataRepository,
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
): ExportDataUseCase {

    override suspend operator fun invoke(uri: Uri) {
        return withContext(dispatcher) {
            exportDataRepository.exportCsv(uri, todoDao.stream(), TODOS_CSV_FILE_NAME, TODOS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, noteDao.stream(), NOTES_CSV_FILE_NAME, NOTES_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, expenseDao.stream(), EXPENSES_CSV_FILE_NAME, EXPENSES_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, investmentDao.stream(), INVESTMENTS_CSV_FILE_NAME, INVESTMENTS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, savingDao.stream(), SAVINGS_CSV_FILE_NAME, SAVINGS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, budgetDao.stream(), BUDGETS_CSV_FILE_NAME, BUDGETS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, watchlistAssetDao.stream(), WATCHLIST_ASSETS_CSV_FILE_NAME, WATCHLIST_ASSETS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, weightDao.stream(), WEIGHTS_CSV_FILE_NAME, WEIGHTS_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, fuelConsumptionDao.stream(), FUEL_CONSUMPTION_CSV_FILE_NAME, FUEL_CONSUMPTION_CSV_MAPPING)
            exportDataRepository.exportCsv(uri, carMaintenanceDao.stream(), CAR_MAINTENANCE_CSV_FILE_NAME, CAR_MAINTENANCE_CSV_MAPPING)
        }
    }

}
