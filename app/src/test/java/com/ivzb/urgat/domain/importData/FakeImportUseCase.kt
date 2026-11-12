package com.ivzb.urgat.domain.importData

import android.net.Uri
import com.ivzb.urgat.ui.importData.ImportDataPage

class FakeImportUseCase : ImportDataUseCase {

    var invoked: Boolean = false
    var invokedExpenses: Boolean = false
    var invokedInvestments: Boolean = false
    var invokedSavings: Boolean = false
    var invokedBudgets: Boolean = false
    var invokedTodos: Boolean = false
    var invokedNotes: Boolean = false
    var invokedWatchlistAssets: Boolean = false
    var invokedWeights: Boolean = false
    var invokedFuelConsumption: Boolean = false
    var invokedCarMaintenance: Boolean = false
    var invokedTrades: Boolean = false

    override suspend fun invoke(uri: Uri) {
        invoked = true
    }

    override suspend fun importManualData(page: ImportDataPage, content: String) {
        when (page) {
            ImportDataPage.Todos -> invokedTodos = true
            ImportDataPage.Notes -> invokedNotes = true
            ImportDataPage.Expenses -> invokedExpenses = true
            ImportDataPage.Investments -> invokedInvestments = true
            ImportDataPage.Savings -> invokedSavings = true
            ImportDataPage.Budgets -> invokedBudgets = true
            ImportDataPage.WatchlistAssets -> invokedWatchlistAssets = true
            ImportDataPage.Weights -> invokedWeights = true
            ImportDataPage.FuelConsumption -> invokedFuelConsumption = true
            ImportDataPage.CarMaintenance -> invokedCarMaintenance = true
            ImportDataPage.Trades -> invokedTrades = true
        }
    }

}
