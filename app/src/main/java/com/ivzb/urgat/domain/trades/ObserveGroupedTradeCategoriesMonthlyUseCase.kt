package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.data.trade.TradeTotal
import com.ivzb.urgat.data.trade.TradesRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.GroupTrades.groupTradeCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

typealias GroupedTradeCategories = GroupedItems<String, List<TradeTotal>, TradeCategory>

interface ObserveGroupedTradeCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedTradeCategories>>>

}

class DefaultObserveGroupedTradeCategoriesMonthlyUseCase @Inject constructor(
    tradesRepository: TradesRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedTradeCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = tradesRepository,
        dispatcher = dispatcher,
        filter = ::filterTrades,
        sort = ::sortTrades,
        group = { trades -> groupTradeCategoriesMonthly(trades, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedTradeCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
