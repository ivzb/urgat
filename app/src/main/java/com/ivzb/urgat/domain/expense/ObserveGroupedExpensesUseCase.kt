package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.domain.default.ObserveGroupedUseCase
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.toLocalDate
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.groupBy

interface ObserveGroupedExpensesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedItems<String, Expense>>>>

}

class DefaultGroupedObserveExpensesUseCase @Inject constructor(
    repository: ExpensesRepository
): ObserveGroupedExpensesUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filter,
        group = ::group,
        errorMessageResId = R.string.error_expenses,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<ExpensesFilterType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedItems<String, Expense>>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

    companion object {

        private val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

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

        fun group(items: List<Expense>): List<GroupedItems<String, Expense>> {
            return items
                .groupBy {
                    val date = it.date.toLocalDate()
                    YearMonth.from(date)
                }
                .map { (yearMonth, expenses) ->
                    GroupedItems(
                        group = yearMonth,
                        items = expenses.sortedByDescending { it.date }
                    )
                }
                .sortedByDescending { it.group }
                .map {
                    GroupedItems(
                        group = it.group.format(formatter),
                        items = it.items
                    )
                }
        }

    }

}