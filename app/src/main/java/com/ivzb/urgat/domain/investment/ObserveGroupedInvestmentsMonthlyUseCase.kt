package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ObserveGroupedInvestmentsMonthlyUseCase {

    val isRefreshingFlow: StateFlow<Boolean>

    operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestments>>>

}

class DefaultObserveGroupedInvestmentsMonthlyUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveGroupedInvestmentsMonthlyUseCase {

    override val isRefreshingFlow: StateFlow<Boolean> = repository.isRefreshingFlow

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
        group = { items -> Group.byDateMonthly(items, Investment::date, Investment::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestments>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}