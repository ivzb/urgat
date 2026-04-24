package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveTradesSortingUseCase {

    operator fun invoke(): Flow<Async<List<TradesSortMenuItem>>>

}

class DefaultObserveTradesSortingUseCase @Inject constructor() : ObserveTradesSortingUseCase {

    override fun invoke(): Flow<Async<List<TradesSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = TradesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = TradesSortType.BY_COST, labelResId = R.string.sort_by_cost),
        )))
    }

}

