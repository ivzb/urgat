package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.domain.default.DefaultObserveUseCase
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveExpensesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Expense>>>

}

class DefaultObserveExpensesUseCase @Inject constructor(
    repository: ExpensesRepository
): ObserveExpensesUseCase {

    private val defaultObserveUseCase = DefaultObserveUseCase(
        repository = repository,
        filter = ::filter,
        errorMessageResId = R.string.error_expenses,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Expense>>> = defaultObserveUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

    companion object {

        private fun filter(items: List<Expense>, filterType: ExpensesFilterType): List<Expense> {
            if (filterType == ExpensesFilterType.ALL_EXPENSES) {
                return items
            }

            return items.filter {
                when (filterType) {
                    ExpensesFilterType.ALL_EXPENSES -> true
                    ExpensesFilterType.COST_EXPENSES -> it.isCost
                    ExpensesFilterType.DEBIT_EXPENSES -> it.isDebit
                }
            }
        }

    }

}