package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.di.IoDispatcher
import com.ivzb.urgat.ui.importData.ImportDataPage
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface ImportDataUseCase {

    suspend operator fun invoke(uri: Uri)

    suspend fun importManualData(page: ImportDataPage, content: String)

}

class DefaultImportDataUseCase @Inject constructor(
    private val importTodosUseCase: ImportTodosUseCase,
    private val importNotesUseCase: ImportNotesUseCase,
    private val importExpensesUseCase: ImportExpensesUseCase,
    private val importInvestmentsUseCase: ImportInvestmentsUseCase,
    private val importSavingsUseCase: ImportSavingsUseCase,
    private val importBudgetsUseCase: ImportBudgetsUseCase,
    private val importWatchlistAssetsUseCase: ImportWatchlistAssetsUseCase,
    private val importWeightsUseCase: ImportWeightsUseCase,
    private val importFuelConsumptionUseCase: ImportFuelConsumptionUseCase,
    private val importCarMaintenanceUseCase: ImportCarMaintenanceUseCase,
    private val importTradesUseCase: ImportTradesUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): ImportDataUseCase {

    override suspend operator fun invoke(uri: Uri) = withContext(dispatcher) {
        importTodosUseCase(uri)
        importNotesUseCase(uri)
        importExpensesUseCase(uri)
        importInvestmentsUseCase(uri)
        importSavingsUseCase(uri)
        importBudgetsUseCase(uri)
        importWatchlistAssetsUseCase(uri)
        importWeightsUseCase(uri)
        importFuelConsumptionUseCase(uri)
        importCarMaintenanceUseCase(uri)
        importTradesUseCase(uri)
    }

    override suspend fun importManualData(page: ImportDataPage, content: String) {
        when (page) {
            ImportDataPage.Expenses -> importExpensesUseCase.importExpenses(content)
            ImportDataPage.Investments -> importInvestmentsUseCase.importInvestments(content)
            ImportDataPage.Trades -> importTradesUseCase.importTrades(content)
            ImportDataPage.Savings -> importSavingsUseCase.importSavings(content)
            ImportDataPage.Budgets -> importBudgetsUseCase.importBudgets(content)
            ImportDataPage.Notes -> importNotesUseCase.importNotes(content)
            ImportDataPage.WatchlistAssets -> importWatchlistAssetsUseCase.importWatchlistAssets(content)
            ImportDataPage.Todos -> importTodosUseCase.importTodos(content)
            ImportDataPage.FuelConsumption -> importFuelConsumptionUseCase.importFuelConsumption(content)
            ImportDataPage.CarMaintenance -> importCarMaintenanceUseCase.importCarMaintenance(content)
            ImportDataPage.Weights -> importWeightsUseCase.importWeights(content)
        }
    }

}
