package com.ivzb.urgat.domain.trades

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortMenuItem
import com.ivzb.urgat.ui.trades.TradesSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveTradesSortingUseCase {

    operator fun invoke(): Flow<Async<List<TradesSortMenuItem>>>

}

class DefaultObserveTradesSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveTradesSortingUseCase {

    override fun invoke(): Flow<Async<List<TradesSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = TradesSortType.BY_DATE, labelResId = R.string.sort_by_date),
            )
        )
    ).flowOn(dispatcher)

}
