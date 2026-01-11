package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveExpensesSortingUseCase {

    operator fun invoke(): Flow<Async<List<ExpensesSortMenuItem>>>

}

class DefaultObserveExpensesSortingUseCase @Inject constructor(
    private val repository: ExpensesRepository
) : ObserveExpensesSortingUseCase {

    override fun invoke(): Flow<Async<List<ExpensesSortMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT)
            .map {
                listOf(
                    SortMenuItem(type = ExpensesSortType.BY_DATE, labelResId = R.string.sort_by_date),
                    SortMenuItem(type = ExpensesSortType.BY_COST, labelResId = R.string.sort_by_cost),
                )
            }
            .map<List<ExpensesSortMenuItem>, Async<List<ExpensesSortMenuItem>>> { Async.Success(it) }
            .catch { emit(Async.Error) }
    }

}
