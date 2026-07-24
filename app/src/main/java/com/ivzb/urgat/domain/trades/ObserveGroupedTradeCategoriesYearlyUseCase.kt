package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.domain.common.Group.groupTradeCategoriesYearly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedTradeCategoriesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedTradeCategories>>>

}

class DefaultObserveGroupedTradeCategoriesYearlyUseCase @Inject constructor(
    tradesRepository: TradesRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedTradeCategoriesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = tradesRepository,
        filter = ::filterTrades,
        sort = ::sortTrades,
        group = { trades -> groupTradeCategoriesYearly(trades, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedTradeCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}

