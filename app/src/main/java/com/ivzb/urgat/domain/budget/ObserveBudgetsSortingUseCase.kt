package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.ui.budgets.BudgetsSortMenuItem
import com.ivzb.urgat.ui.budgets.BudgetsSortType
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveBudgetsSortingUseCase {

    operator fun invoke(): Flow<Async<List<BudgetsSortMenuItem>>>

}

class DefaultObserveBudgetsSortingUseCase @Inject constructor(
    private val repository: BudgetsRepository
) : ObserveBudgetsSortingUseCase {

    override fun invoke(): Flow<Async<List<BudgetsSortMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT)
            .map {
                listOf(
                    SortMenuItem(type = BudgetsSortType.BY_DATE, labelResId = R.string.sort_by_date),
                    SortMenuItem(type = BudgetsSortType.BY_MIN, labelResId = R.string.sort_by_min),
                    SortMenuItem(type = BudgetsSortType.BY_MAX, labelResId = R.string.sort_by_max),
                )
            }
            .map<List<BudgetsSortMenuItem>, Async<List<BudgetsSortMenuItem>>> { Async.Success(it) }
            .catch { emit(Async.Error) }
    }

}
