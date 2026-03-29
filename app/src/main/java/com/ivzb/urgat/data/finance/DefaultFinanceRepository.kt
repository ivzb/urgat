package com.ivzb.urgat.data.finance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.dataStoreEnumFlow
import com.ivzb.urgat.util.dataStoreStringFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultFinanceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : FinanceRepository {

    override val searchQueryFlow: Flow<String> = dataStoreStringFlow(dataStore, SEARCH_QUERY, "")

    override suspend fun updateSearchQuery(value: String) {
        dataStore.edit { it[SEARCH_QUERY] = value }
    }

    override val expensesFilterTypeFlow: Flow<ExpensesFilterType> = dataStoreEnumFlow(dataStore, EXPENSES_FILTER_TYPE, ExpensesFilterType.ALL_EXPENSES)

    override suspend fun updateExpensesFilterType(value: ExpensesFilterType) {
        dataStore.edit { it[EXPENSES_FILTER_TYPE] = value.name }
    }

    override val expensesSortTypeFlow: Flow<ExpensesSortType> = dataStoreEnumFlow(dataStore, EXPENSES_SORT_TYPE, ExpensesSortType.BY_DATE)

    override suspend fun updateExpensesSortType(value: ExpensesSortType) {
        dataStore.edit { it[EXPENSES_SORT_TYPE] = value.name }
    }

    override val investmentsFilterTypeFlow: Flow<String> = dataStoreStringFlow(dataStore, INVESTMENTS_FILTER_TYPE, InvestmentsFilterType_All)

    override suspend fun updateInvestmentsFilterType(value: String) {
        dataStore.edit { it[INVESTMENTS_FILTER_TYPE] = value }
    }

    override val investmentsSortTypeFlow: Flow<InvestmentsSortType> = dataStoreEnumFlow(dataStore, INVESTMENTS_SORT_TYPE, InvestmentsSortType.BY_DATE)

    override suspend fun updateInvestmentsSortType(value: InvestmentsSortType) {
        dataStore.edit { it[INVESTMENTS_SORT_TYPE] = value.name }
    }

    override val savingsSortTypeFlow: Flow<SavingsSortType> = dataStoreEnumFlow(dataStore, SAVINGS_SORT_TYPE, SavingsSortType.BY_DATE)

    override suspend fun updateSavingsSortType(value: SavingsSortType) {
        dataStore.edit { it[SAVINGS_SORT_TYPE] = value.name }
    }

    override val budgetsFilterTypeFlow: Flow<BudgetsFilterType> = dataStoreEnumFlow(dataStore, BUDGETS_FILTER_TYPE, BudgetsFilterType.ALL_BUDGETS)

    override suspend fun updateBudgetsFilterType(value: BudgetsFilterType) {
        dataStore.edit { it[BUDGETS_FILTER_TYPE] = value.name }
    }

    override val budgetsSortTypeFlow: Flow<BudgetsSortType> = dataStoreEnumFlow(dataStore, BUDGETS_SORT_TYPE, BudgetsSortType.BY_DATE)

    override suspend fun updateBudgetsSortType(value: BudgetsSortType) {
        dataStore.edit { it[BUDGETS_SORT_TYPE] = value.name }
    }

    companion object {

        private val SEARCH_QUERY = stringPreferencesKey("finance_search_query")

        private val EXPENSES_FILTER_TYPE = stringPreferencesKey("finance_expenses_filter_type")
        private val EXPENSES_SORT_TYPE = stringPreferencesKey("finance_expenses_sort_type")

        private val INVESTMENTS_FILTER_TYPE = stringPreferencesKey("finance_investments_filter_type")
        private val INVESTMENTS_SORT_TYPE = stringPreferencesKey("finance_investments_sort_type")

        private val SAVINGS_SORT_TYPE = stringPreferencesKey("finance_savings_sort_type")

        private val BUDGETS_FILTER_TYPE = stringPreferencesKey("finance_budgets_filter_type")
        private val BUDGETS_SORT_TYPE = stringPreferencesKey("finance_budgets_sort_type")

    }

}