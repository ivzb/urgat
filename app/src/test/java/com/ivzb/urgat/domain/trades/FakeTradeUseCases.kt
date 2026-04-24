package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.ui.trades.TradesFilterMenuItem
import com.ivzb.urgat.ui.trades.TradesSortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FakeObserveGroupedTradesMonthlyUseCase : ObserveGroupedTradesMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedTrades>>>(Async.Loading)

    private val _isRefreshingFlow = MutableStateFlow(false)
    override val isRefreshingFlow: StateFlow<Boolean> = _isRefreshingFlow

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedTrades>>) {
        flow.value = value
    }

    fun emitRefreshing(isRefreshing: Boolean) {
        _isRefreshingFlow.value = isRefreshing
    }

    override operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<TradesSortType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<GroupedTrades>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveTradesFilteringUseCase : ObserveTradesFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<TradesFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<TradesFilterMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<TradesFilterMenuItem>>> {
        return flow
    }

}

class FakeObserveTradesSortingUseCase : ObserveTradesSortingUseCase {

    private val flow = MutableStateFlow<Async<List<TradesSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<TradesSortMenuItem>>) {
        flow.value = value
    }

    override operator fun invoke(): Flow<Async<List<TradesSortMenuItem>>> {
        return flow
    }

}

