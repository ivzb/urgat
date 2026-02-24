package com.ivzb.urgat.data.finance

import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {

    val searchQueryFlow: Flow<String>
    suspend fun updateSearchQuery(value: String)

    val expensesFilterTypeFlow: Flow<ExpensesFilterType>
    val expensesSortTypeFlow: Flow<ExpensesSortType>
    suspend fun updateExpensesFilterType(value: ExpensesFilterType)
    suspend fun updateExpensesSortType(value: ExpensesSortType)

    val investmentsFilterTypeFlow: Flow<String>
    val investmentsSortTypeFlow: Flow<InvestmentsSortType>
    suspend fun updateInvestmentsFilterType(value: String)
    suspend fun updateInvestmentsSortType(value: InvestmentsSortType)

    val savingsSortTypeFlow: Flow<SavingsSortType>
    suspend fun updateSavingsSortType(value: SavingsSortType)

    val budgetsFilterTypeFlow: Flow<BudgetsFilterType>
    val budgetsSortTypeFlow: Flow<BudgetsSortType>
    suspend fun updateBudgetsFilterType(value: BudgetsFilterType)
    suspend fun updateBudgetsSortType(value: BudgetsSortType)

}

