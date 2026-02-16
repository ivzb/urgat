package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType

sealed class FinanceIntent {
    data class SearchQueryChanged(val query: String): FinanceIntent()
    data class FabActionClick(val itemId: FinanceFabActionId): FinanceIntent()
    data class UpdateFireWithdrawalRate(val withdrawalRate: String): FinanceIntent()
    data class UpdateFireReturnOfInvestment(val returnOfInvestment: String): FinanceIntent()
    data class UpdateFireTaxRate(val taxRate: String): FinanceIntent()

    data class SetExpensesFiltering(val filterType: ExpensesFilterType): FinanceIntent()
    data class SetExpensesSorting(val sortType: ExpensesSortType): FinanceIntent()

    data class SetInvestmentsFiltering(val filterType: String): FinanceIntent()
    data class SetInvestmentsSorting(val sortType: InvestmentsSortType): FinanceIntent()

    data class SetSavingsSorting(val sortType: SavingsSortType): FinanceIntent()

    data class SetBudgetsFiltering(val filterType: BudgetsFilterType): FinanceIntent()
    data class SetBudgetsSorting(val sortType: BudgetsSortType): FinanceIntent()

    object EventConsumed: FinanceIntent()
}