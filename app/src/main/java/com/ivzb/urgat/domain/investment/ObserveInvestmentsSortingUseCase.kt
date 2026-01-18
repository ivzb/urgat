package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface ObserveInvestmentsSortingUseCase {

    operator fun invoke(): Flow<Async<List<InvestmentsSortMenuItem>>>

}

class DefaultObserveInvestmentsSortingUseCase @Inject constructor() : ObserveInvestmentsSortingUseCase {

    override fun invoke(): Flow<Async<List<InvestmentsSortMenuItem>>> {
        return flowOf(Async.Success(listOf(
            SortMenuItem(type = InvestmentsSortType.BY_DATE, labelResId = R.string.sort_by_date),
            SortMenuItem(type = InvestmentsSortType.BY_MARKET_VALUE, labelResId = R.string.sort_by_market_value),
        )))
    }

}
