package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveGroupedInvestmentsYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestments>>>

}

class DefaultObserveGroupedInvestmentsYearlyUseCase @Inject constructor(
    repository: InvestmentsRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedInvestmentsYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
        group = { items -> Group.byDateYearly(items, Investment::date, Investment::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestments>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
