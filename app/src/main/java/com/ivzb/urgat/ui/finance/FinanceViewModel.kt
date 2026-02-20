package com.ivzb.urgat.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.fire.FireRepository
import com.ivzb.urgat.domain.budget.ObserveBudgetsUiStateUseCase
import com.ivzb.urgat.domain.budget.ObserveReportBudgetUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUiStateUseCase
import com.ivzb.urgat.domain.expense.ObserveGroupedExpensesMonthlyUseCase
import com.ivzb.urgat.domain.fire.ObserveFiReUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUiStateUseCase
import com.ivzb.urgat.domain.saving.ObserveSavingsUiStateUseCase
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import com.ivzb.urgat.util.currentMonthRange
import com.ivzb.urgat.util.untilCurrentMonthRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    observeExpensesUiStateUseCase: ObserveExpensesUiStateUseCase,
    observeInvestmentsUiStateUseCase: ObserveInvestmentsUiStateUseCase,
    observeSavingsUiStateUseCase: ObserveSavingsUiStateUseCase,
    observeBudgetsUiStateUseCase: ObserveBudgetsUiStateUseCase,
    observeReportBudgetUseCase: ObserveReportBudgetUseCase,
    observeFiReUseCase: ObserveFiReUseCase,
    private val fireRepository: FireRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<FinanceUiEvent>()

    private val _searchQuery = MutableStateFlow("")

    private val _budgetDateRange = MutableStateFlow(currentMonthRange())

    private val _withdrawalRate = fireRepository.withdrawalRateFlow

    private val _returnOfInvestment = fireRepository.returnOfInvestmentFlow

    private val _taxRate = fireRepository.taxRateFlow

    private val _fireDateRange = MutableStateFlow(untilCurrentMonthRange())

    private val _savedExpensesFilterType: Flow<ExpensesFilterType> = savedStateHandle.getStateFlow(FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY, ExpensesFilterType.ALL_EXPENSES)
    
    private val _savedExpensesSortType: Flow<ExpensesSortType> = savedStateHandle.getStateFlow(FINANCE_EXPENSES_SORT_SAVED_STATE_KEY, ExpensesSortType.BY_DATE)

    private val _savedInvestmentsFilterType = savedStateHandle.getStateFlow(FINANCE_INVESTMENTS_FILTER_SAVED_STATE_KEY, initialValue = InvestmentsFilterType_All)

    private val _savedInvestmentsSortType: Flow<InvestmentsSortType> = savedStateHandle.getStateFlow(FINANCE_INVESTMENTS_SORT_SAVED_STATE_KEY, InvestmentsSortType.BY_DATE)

    private val _savedSavingsSortType: Flow<SavingsSortType> = savedStateHandle.getStateFlow(FINANCE_SAVINGS_SORT_SAVED_STATE_KEY, SavingsSortType.BY_DATE)

    private val _savedFilterType: Flow<BudgetsFilterType> = savedStateHandle.getStateFlow(FINANCE_BUDGETS_FILTER_SAVED_STATE_KEY, initialValue = BudgetsFilterType.ALL_BUDGETS)

    private val _savedSortType: Flow<BudgetsSortType> = savedStateHandle.getStateFlow(FINANCE_BUDGETS_SORT_SAVED_STATE_KEY, BudgetsSortType.BY_DATE)

    val uiState: StateFlow<FinanceUiState> = combineUiState(
        observeExpensesUiStateUseCase(_searchQuery, _savedExpensesFilterType, _savedExpensesSortType),
        observeInvestmentsUiStateUseCase(_searchQuery, _savedInvestmentsFilterType, _savedInvestmentsSortType),
        observeSavingsUiStateUseCase(_searchQuery, _savedSavingsSortType),
        observeBudgetsUiStateUseCase(_searchQuery, _savedFilterType, _savedSortType),
        observeReportBudgetUseCase(_searchQuery, _budgetDateRange, null, null, null),
        observeFiReUseCase(_withdrawalRate, _returnOfInvestment, _taxRate, _searchQuery, _fireDateRange, null, null, null),
        uiEventHolder(),

        onLoading = {
            FinanceUiState(isLoading = true)
        },
        onError = {
            FinanceUiState(hasError = true)
        },
        onSuccess = { expensesUiState, investmentsUiState, savingsUiState, budgetsUiState, reportBudget, fire, uiEvent ->
            FinanceUiState(
                expensesUiState = expensesUiState,
                investmentsUiState = investmentsUiState,
                savingsUiState = savingsUiState,
                budgetsUiState = budgetsUiState,
                reportBudget = reportBudget,
                fire = fire,
                isLoading = false,
                uiEvent = uiEvent,
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = FinanceUiState(isLoading = true)
    )

    fun onIntent(intent: FinanceIntent) {
        when (intent) {
            is FinanceIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is FinanceIntent.FabActionClick -> onFabActionClick(intent.itemId)
            is FinanceIntent.UpdateFireWithdrawalRate -> onUpdateFireWithdrawalRate(intent.withdrawalRate)
            is FinanceIntent.UpdateFireReturnOfInvestment -> onUpdateFireReturnOfInvestment(intent.returnOfInvestment)
            is FinanceIntent.UpdateFireTaxRate -> onUpdateFireTaxRate(intent.taxRate)

            is FinanceIntent.SetExpensesFiltering -> setExpensesFiltering(intent.filterType)
            is FinanceIntent.SetExpensesSorting -> setExpensesSorting(intent.sortType)

            is FinanceIntent.SetInvestmentsFiltering -> setInvestmentsFiltering(intent.filterType)
            is FinanceIntent.SetInvestmentsSorting -> setInvestmentsSorting(intent.sortType)

            is FinanceIntent.SetSavingsSorting -> setSavingsSorting(intent.sortType)

            is FinanceIntent.SetBudgetsFiltering -> setBudgetsFiltering(intent.filterType)
            is FinanceIntent.SetBudgetsSorting -> setBudgetsSorting(intent.sortType)

            is FinanceIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun onFabActionClick(id: FinanceFabActionId) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (id) {
                    FinanceFabActionId.Expense -> FinanceUiEvent.AddExpense
                    FinanceFabActionId.Investment -> FinanceUiEvent.AddInvestment
                    FinanceFabActionId.Saving -> FinanceUiEvent.AddSaving
                    FinanceFabActionId.Budget -> FinanceUiEvent.AddBudget
                }
            )
        }
    }

    private fun onUpdateFireWithdrawalRate(withdrawalRate: String) {
        viewModelScope.launch {
            fireRepository.updateWithdrawalRate(withdrawalRate)
        }
    }

    private fun onUpdateFireReturnOfInvestment(returnOfInvestment: String) {
        viewModelScope.launch {
            fireRepository.updateReturnOfInvestment(returnOfInvestment)
        }
    }

    private fun onUpdateFireTaxRate(taxRate: String) {
        viewModelScope.launch {
            fireRepository.updateTaxRate(taxRate)
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

    private fun setExpensesFiltering(filterType: ExpensesFilterType) {
        savedStateHandle[FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setExpensesSorting(sortType: ExpensesSortType) {
        savedStateHandle[FINANCE_EXPENSES_SORT_SAVED_STATE_KEY] = sortType
    }

    private fun setInvestmentsFiltering(filterType: String) {
        savedStateHandle[FINANCE_INVESTMENTS_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setInvestmentsSorting(sortType: InvestmentsSortType) {
        savedStateHandle[FINANCE_INVESTMENTS_SORT_SAVED_STATE_KEY] = sortType
    }

    private fun setSavingsSorting(sortType: SavingsSortType) {
        savedStateHandle[FINANCE_SAVINGS_SORT_SAVED_STATE_KEY] = sortType
    }

    private fun setBudgetsFiltering(filterType: BudgetsFilterType) {
        savedStateHandle[FINANCE_BUDGETS_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setBudgetsSorting(sortType: BudgetsSortType) {
        savedStateHandle[FINANCE_BUDGETS_SORT_SAVED_STATE_KEY] = sortType
    }

    companion object {

        const val FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY = "FINANCE_EXPENSES_FILTER_SAVED_STATE_KEY"
        const val FINANCE_EXPENSES_SORT_SAVED_STATE_KEY = "FINANCE_EXPENSES_SORT_SAVED_STATE_KEY"

        const val FINANCE_INVESTMENTS_FILTER_SAVED_STATE_KEY = "FINANCE_INVESTMENTS_FILTER_SAVED_STATE_KEY"
        const val FINANCE_INVESTMENTS_SORT_SAVED_STATE_KEY = "FINANCE_INVESTMENTS_SORT_SAVED_STATE_KEY"

        const val FINANCE_SAVINGS_SORT_SAVED_STATE_KEY = "FINANCE_SAVINGS_SORT_SAVED_STATE_KEY"

        const val FINANCE_BUDGETS_FILTER_SAVED_STATE_KEY = "FINANCE_BUDGETS_FILTER_SAVED_STATE_KEY"
        const val FINANCE_BUDGETS_SORT_SAVED_STATE_KEY = "FINANCE_BUDGETS_SORT_SAVED_STATE_KEY"

    }

}