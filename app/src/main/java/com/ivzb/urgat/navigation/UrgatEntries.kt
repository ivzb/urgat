package com.ivzb.urgat.navigation

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.ivzb.urgat.ui.about.aboutEntry
import com.ivzb.urgat.ui.addEditBudget.addEditBudgetEntry
import com.ivzb.urgat.ui.addEditExpense.addEditExpenseEntry
import com.ivzb.urgat.ui.addEditInvestment.addEditInvestmentEntry
import com.ivzb.urgat.ui.addEditNote.addEditNoteEntry
import com.ivzb.urgat.ui.addEditSaving.addEditSavingEntry
import com.ivzb.urgat.ui.addEditTodo.addEditTodoEntry
import com.ivzb.urgat.ui.exportData.exportDataEntry
import com.ivzb.urgat.ui.finance.financeEntry
import com.ivzb.urgat.ui.importData.importDataEntry
import com.ivzb.urgat.ui.library.libraryEntry
import com.ivzb.urgat.ui.sample.sampleEntry
import com.ivzb.urgat.ui.addEditWeight.addEditWeightEntry
import com.ivzb.urgat.ui.budgets.budgetsEntry
import com.ivzb.urgat.ui.weights.weightsEntry
import com.ivzb.urgat.ui.wellbeing.wellbeingEntry

fun urgatEntryProvider(navigator: Navigator, navActions: UrgatNavActions): (NavKey) -> NavEntry<NavKey> {
    return entryProvider {
        libraryEntry(navigator, navActions)
        financeEntry(navigator, navActions)
        wellbeingEntry(navigator, navActions)

        addEditTodoEntry(navigator)
        addEditNoteEntry(navigator)
        addEditExpenseEntry(navigator)
        addEditInvestmentEntry(navigator)
        addEditSavingEntry(navigator)

        budgetsEntry(navigator, navActions)
        addEditBudgetEntry(navigator)

        weightsEntry(navigator, navActions)
        addEditWeightEntry(navigator)

        sampleEntry(navigator)

        exportDataEntry(navigator)
        importDataEntry(navigator)

        aboutEntry(navigator)
    }
}