package com.ivzb.urgat.ui.importData

import androidx.annotation.StringRes
import com.ivzb.urgat.R

sealed class ImportDataPage(
    @StringRes
    val titleResId: Int
) {
    data object Expenses: ImportDataPage(R.string.expenses)
    data object Investments: ImportDataPage(R.string.investments)
    data object Savings: ImportDataPage(R.string.savings)
    data object Budgets: ImportDataPage(R.string.budgets)
    data object Todos: ImportDataPage(R.string.todo)
    data object Notes: ImportDataPage(R.string.notes)
    data object WatchlistAssets: ImportDataPage(R.string.watchlist)
    data object Weights: ImportDataPage(R.string.weights)
}

val IMPORT_DATA_PAGES = listOf(
    ImportDataPage.Expenses,
    ImportDataPage.Investments,
    ImportDataPage.Savings,
    ImportDataPage.Budgets,
    ImportDataPage.Todos,
    ImportDataPage.Notes,
    ImportDataPage.WatchlistAssets,
    ImportDataPage.Weights,
)