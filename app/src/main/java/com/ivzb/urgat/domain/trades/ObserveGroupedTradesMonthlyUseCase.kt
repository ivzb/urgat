package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ObserveGroupedTradesMonthlyUseCase {

    val isRefreshingFlow: StateFlow<Boolean>

    operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedTrades>>>

}

class DefaultObserveGroupedTradesMonthlyUseCase @Inject constructor(
    repository: TradesRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedTradesMonthlyUseCase {

    override val isRefreshingFlow: StateFlow<Boolean> = repository.isRefreshingFlow

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterTrades,
        sort = ::sortTrades,
        group = { items -> Group.byDateMonthly(items, Trade::date, Trade::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedTrades>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
