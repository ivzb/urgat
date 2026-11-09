package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.trades.TRADES_FILTER_TYPE_ALL
import com.ivzb.urgat.ui.trades.TradesFilteringUiInfo
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.ui.trades.TradesUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface ObserveTradesUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<TradesSortType>,
    ): Flow<Async<TradesUiState>>

}

class DefaultObserveTradesUiStateUseCase @Inject constructor(
    private val observeGroupedTradesMonthlyUseCase: ObserveGroupedTradesMonthlyUseCase,
    private val observeGroupedTradeCategoriesMonthlyUseCase: ObserveGroupedTradeCategoriesMonthlyUseCase,
    private val observeGroupedTradeCategoriesYearlyUseCase: ObserveGroupedTradeCategoriesYearlyUseCase,
    private val observeGroupedTradeCategoriesAllTimeUseCase: ObserveGroupedTradeCategoriesAllTimeUseCase,
    private val observeTradesSortingUseCase: ObserveTradesSortingUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
): ObserveTradesUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<TradesSortType>,
    ): Flow<Async<TradesUiState>> = combineUiState(
        observeGroupedTradesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedTradeCategoriesMonthlyUseCase(searchQueryFlow, dateRangeFlow = null, null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedTradeCategoriesYearlyUseCase(searchQueryFlow, dateRangeFlow = null, null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedTradeCategoriesAllTimeUseCase(searchQueryFlow, dateRangeFlow = null, null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeTradesSortingUseCase(),
        combine(searchQueryFlow, sortTypeFlow, ::Pair).map { Async.Success(it) },

        onLoading = {
            TradesUiState(isLoading = true)
        },
        onError = {
            TradesUiState(hasError = true)
        },
        onSuccess = { trades, categoriesMonthly, categoriesYearly, categoriesAllTime, sorting, (searchQuery, sortType) ->
            TradesUiState(
                trades = trades,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                filteringUiInfo = TradesFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    filterType = TRADES_FILTER_TYPE_ALL,
                    filterMenuItems = listOf(),
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
                isLoading = false,
            )
        }
    ).map { Async.Success(it) }
        .flowOn(dispatcher)

}
