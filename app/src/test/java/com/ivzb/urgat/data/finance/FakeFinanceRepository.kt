package com.ivzb.urgat.data.finance

import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFinanceRepository : FinanceRepository {

    override val searchQueryFlow = MutableStateFlow("")
    override suspend fun updateSearchQuery(value: String) { searchQueryFlow.value = value }

    override val expensesFilterTypeFlow = MutableStateFlow(ExpensesFilterType.ALL_EXPENSES)
    override val expensesSortTypeFlow = MutableStateFlow(ExpensesSortType.BY_DATE)
    override suspend fun updateExpensesFilterType(value: ExpensesFilterType) { expensesFilterTypeFlow.value = value }
    override suspend fun updateExpensesSortType(value: ExpensesSortType) { expensesSortTypeFlow.value = value }

    override val investmentsFilterTypeFlow = MutableStateFlow(InvestmentsFilterType_All)
    override val investmentsSortTypeFlow = MutableStateFlow(InvestmentsSortType.BY_DATE)
    override suspend fun updateInvestmentsFilterType(value: String) { investmentsFilterTypeFlow.value = value }
    override suspend fun updateInvestmentsSortType(value: InvestmentsSortType) { investmentsSortTypeFlow.value = value }

    override val savingsSortTypeFlow = MutableStateFlow(SavingsSortType.BY_DATE)
    override suspend fun updateSavingsSortType(value: SavingsSortType) { savingsSortTypeFlow.value = value }

    override val budgetsFilterTypeFlow = MutableStateFlow(BudgetsFilterType.ALL_BUDGETS)
    override val budgetsSortTypeFlow = MutableStateFlow(BudgetsSortType.BY_DATE)
    override suspend fun updateBudgetsFilterType(value: BudgetsFilterType) { budgetsFilterTypeFlow.value = value }
    override suspend fun updateBudgetsSortType(value: BudgetsSortType) { budgetsSortTypeFlow.value = value }

}


