package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.investments.INVESTMENTS_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.investments.InvestmentsFilterMenuItem
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface ObserveInvestmentsFilteringUseCase {

    operator fun invoke(): Flow<Async<List<InvestmentsFilterMenuItem>>>

}

class DefaultObserveInvestmentsFilteringUseCase @Inject constructor(
    private val repository: InvestmentsRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveInvestmentsFilteringUseCase {

    override fun invoke(): Flow<Async<List<InvestmentsFilterMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE)
            .map {
                if (it.isEmpty()) {
                    return@map listOf()
                }

                listOf(FilterMenuItem(type = INVESTMENTS_FILTER_TYPE_ALL, labelResId = R.string.filter_all, size = it.size)) +
                    it.groupBy { it.category }
                        .entries
                        .sortedByDescending { it.value.size }
                        .map { FilterMenuItem(type = it.key, label = it.key, size = it.value.size) }
            }
            .map<List<InvestmentsFilterMenuItem>, Async<List<InvestmentsFilterMenuItem>>> { Async.Success(it) }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }
            .flowOn(dispatcher)
    }

}
