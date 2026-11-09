package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group.groupInvestmentNetWorth
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveInvestmentNetWorthUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<InvestmentNetWorth>>

}

class DefaultObserveInvestmentNetWorthUseCase @Inject constructor(
    repository: InvestmentsRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveInvestmentNetWorthUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
        group = ::groupInvestmentNetWorth,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<InvestmentNetWorth>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
