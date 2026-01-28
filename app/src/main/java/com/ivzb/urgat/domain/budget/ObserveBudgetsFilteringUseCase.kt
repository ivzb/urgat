package com.ivzb.urgat.domain.budget

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.budget.BudgetsRepository
import com.ivzb.urgat.ui.budgets.BudgetsFilterMenuItem
import com.ivzb.urgat.ui.budgets.BudgetsFilterType
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject

interface ObserveBudgetsFilteringUseCase {

    operator fun invoke(): Flow<Async<List<BudgetsFilterMenuItem>>>

}

class DefaultObserveBudgetsFilteringUseCase @Inject constructor(
    private val repository: BudgetsRepository
) : ObserveBudgetsFilteringUseCase {

    override fun invoke(): Flow<Async<List<BudgetsFilterMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE)
            .map {
                listOf(
                    FilterMenuItem(type = BudgetsFilterType.ALL_BUDGETS, labelResId = R.string.filter_all, size = it.size),
                    FilterMenuItem(type = BudgetsFilterType.POSITIVE_BUDGETS, labelResId = R.string.filter_positive, size = it.count { it.isPositive }),
                    FilterMenuItem(type = BudgetsFilterType.NEGATIVE_BUDGETS, labelResId = R.string.filter_negative, size = it.count { it.isNegative }),
                )
            }
            .map<List<BudgetsFilterMenuItem>, Async<List<BudgetsFilterMenuItem>>> { Async.Success(it) }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }
    }

}
