package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.ui.budgets.BudgetsFilteringUiInfo
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.uiState.NavigationPagerUiState
import com.ivzb.urgat.ui.components.uiState.NavigationUiState
import com.ivzb.urgat.ui.components.uiState.PagerUiState
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesUiState
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import java.math.BigDecimal

data class FinanceUiState(
    override val fabActions: List<FinanceFabAction> = FINANCE_FAB_ACTIONS,

    val dashboardBudget: GroupedBudget? = null,

    val expensesUiState: ExpensesUiState = ExpensesUiState(),
//    val expenses: List<Expense> = emptyList(),
//    val expensesFilteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo(),

    val investments: List<Investment> = emptyList(),
    val investmentsFilteringUiInfo: InvestmentsFilteringUiInfo = InvestmentsFilteringUiInfo(),

    val savings: List<Saving> = emptyList(),
    val savingsFilteringUiInfo: SavingsFilteringUiInfo = SavingsFilteringUiInfo(),

    val budgets: List<Budget> = emptyList(),
    val budgetsFilteringUiInfo: BudgetsFilteringUiInfo = BudgetsFilteringUiInfo(),

    val fire: FiRe? = null,

    override val isLoading: Boolean = false,
    override val hasError: Boolean = false,
    override val isEmpty: Boolean = false,

    override val pages: List<Page> = FINANCE_PAGES.filter {
        when (it) {
            FinancePage.Dashboard -> dashboardBudget != null
            FinancePage.FIRE -> fire != null
            else -> true
        }
    },
    override val initialPage: Int = 0,

    override val filteringUiInfo: FilteringUiInfo = FinanceFilteringUiInfo(),

    val uiEvent: FinanceUiEvent? = null,
): NavigationPagerUiState<FinanceFabActionId>