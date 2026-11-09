package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

interface ObserveInvestmentsSortingUseCase {

    operator fun invoke(): Flow<Async<List<InvestmentsSortMenuItem>>>

}

class DefaultObserveInvestmentsSortingUseCase @Inject constructor(
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveInvestmentsSortingUseCase {

    override fun invoke(): Flow<Async<List<InvestmentsSortMenuItem>>> = flowOf(
        Async.Success(
            listOf(
                SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
                SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
            )
        )
    ).flowOn(dispatcher)

}
