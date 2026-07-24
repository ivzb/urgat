package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.domain.common.Group.groupTradeCategoriesAllTime
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
): ObserveGroupedTradeCategoriesAllTimeUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = tradesRepository,
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

