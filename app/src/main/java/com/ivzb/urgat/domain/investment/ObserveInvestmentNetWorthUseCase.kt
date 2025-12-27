package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.common.Group.groupInvestmentNetWorth
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentNetWorthUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<InvestmentNetWorth>>

}

class DefaultObserveInvestmentNetWorthUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveInvestmentNetWorthUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterInvestments,
        group = ::groupInvestmentNetWorth,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<InvestmentNetWorth>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}
