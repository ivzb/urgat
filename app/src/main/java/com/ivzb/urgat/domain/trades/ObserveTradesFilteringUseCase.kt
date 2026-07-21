package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.trades.TradesFilterMenuItem
import com.ivzb.urgat.ui.trades.TradesFilterType_All
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveTradesFilteringUseCase {

    operator fun invoke(): Flow<Async<List<TradesFilterMenuItem>>>

}

class DefaultObserveTradesFilteringUseCase @Inject constructor(
    private val repository: TradesRepository
) : ObserveTradesFilteringUseCase {

    override fun invoke(): Flow<Async<List<TradesFilterMenuItem>>> {
        return repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE)
            .map {
                if (it.isEmpty()) {
                    return@map listOf()
                }

                listOf(FilterMenuItem(type = TradesFilterType_All, labelResId = R.string.filter_all, size = it.size))
            }
            .map<List<TradesFilterMenuItem>, Async<List<TradesFilterMenuItem>>> { Async.Success(it) }
            .catch { e ->
                Tracker.error(e)
                emit(Async.Error)
            }
    }

}

