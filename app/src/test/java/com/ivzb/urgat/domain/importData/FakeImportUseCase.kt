package com.ivzb.urgat.domain.importData

import android.net.Uri

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

    override suspend fun invoke(uri: Uri) {
        invoked = true
    }

    override suspend fun importExpenses(content: String) {
        invokedExpenses = true
    }

    override suspend fun importInvestments(content: String) {
        invokedInvestments = true
    }

    override suspend fun importSavings(content: String) {
        invokedSavings = true
    }

    override suspend fun importBudgets(content: String) {
        invokedBudgets = true
    }

    override suspend fun importTodos(content: String) {
        invokedTodos = true
    }

    override suspend fun importNotes(content: String) {
        invokedNotes = true
    }

    override suspend fun importWatchlistAssets(content: String) {
        invokedWatchlistAssets = true
    }

    override suspend fun importWeights(content: String) {
        invokedWeights = true
    }

}
