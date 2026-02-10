package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType

sealed class FinanceIntent {
    data class SearchQueryChanged(val query: String): FinanceIntent()
    data class FabActionClick(val itemId: FinanceFabActionId): FinanceIntent()
    data class UpdateFireWithdrawalRate(val withdrawalRate: String): FinanceIntent()
    data class UpdateFireReturnOfInvestment(val returnOfInvestment: String): FinanceIntent()
    data class UpdateFireTaxRate(val taxRate: String): FinanceIntent()

    data class SetExpensesFiltering(val filterType: ExpensesFilterType): FinanceIntent()
    data class SetExpensesSorting(val sortType: ExpensesSortType): FinanceIntent()

    object EventConsumed: FinanceIntent()
}