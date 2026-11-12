package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.GroupTrades.groupTradeCategoriesAllTime
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveGroupedTradeCategoriesAllTimeUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<TradeCategory>>>

}

class DefaultObserveGroupedTradeCategoriesAllTimeUseCase @Inject constructor(
    tradesRepository: TradesRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedTradeCategoriesAllTimeUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = tradesRepository,
        dispatcher = dispatcher,
        filter = ::filterTrades,
        sort = ::sortTrades,
        group = { trades -> groupTradeCategoriesAllTime(trades, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<TradeCategory>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
