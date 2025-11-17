package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER_ID
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveInvestmentsFilteringUseCase {

    operator fun invoke(): Flow<Async<List<FilterMenuItem>>>

}

class DefaultObserveInvestmentsFilteringUseCase @Inject constructor(
    private val investmentsRepository: InvestmentsRepository
) : ObserveInvestmentsFilteringUseCase {

    override fun invoke(): Flow<Async<List<FilterMenuItem>>> {
        return investmentsRepository.getStream(NO_LIMIT)
            .map {
                listOf(FilterMenuItem(id = CLEAR_FILTER_ID, labelResId = R.string.filter_all, size = it.size)) +
                    it.groupBy { it.category }
                        .entries
                        .sortedByDescending { it.value.size }
                        .map { FilterMenuItem(id = it.key, label = it.key, size = it.value.size) }
            }
            .map<List<FilterMenuItem>, Async<List<FilterMenuItem>>> { Async.Success(it) }
            .catch { emit(Async.Error(R.string.error_investments_filtering)) }
    }

}
