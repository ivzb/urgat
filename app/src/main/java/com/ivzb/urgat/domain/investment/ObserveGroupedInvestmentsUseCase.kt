package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias GroupedInvestments = GroupedItems<String, String, Investment>

interface ObserveGroupedInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestments>>>

}

class DefaultObserveGroupedInvestmentsUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveGroupedInvestmentsUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterInvestments,
        group = { items -> Group.byDateMonthly(items, Investment::date, Investment::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestments>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}