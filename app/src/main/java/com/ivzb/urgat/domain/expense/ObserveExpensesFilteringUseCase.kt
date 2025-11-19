package com.ivzb.urgat.domain.expense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveExpensesFilteringUseCase {

    operator fun invoke(): Flow<Async<List<ExpensesFilterMenuItem>>>

}

class DefaultObserveExpensesFilteringUseCase @Inject constructor(
    private val expensesRepository: ExpensesRepository
) : ObserveExpensesFilteringUseCase {

    override fun invoke(): Flow<Async<List<ExpensesFilterMenuItem>>> {
        return expensesRepository.getStream(NO_LIMIT)
            .map {
                listOf(
                    FilterMenuItem(type = ExpensesFilterType.ALL_EXPENSES, labelResId = R.string.filter_all, size = it.size),
                    FilterMenuItem(type = ExpensesFilterType.DEBIT_EXPENSES, labelResId = R.string.filter_debit, size = it.count { it.isDebit}),
                    FilterMenuItem(type = ExpensesFilterType.COST_EXPENSES, labelResId = R.string.filter_cost, size = it.count { it.isCost }),
                )
            }
            .map<List<ExpensesFilterMenuItem>, Async<List<ExpensesFilterMenuItem>>> { Async.Success(it) }
            .catch { emit(Async.Error(R.string.error_expenses_filtering)) }
    }

}
