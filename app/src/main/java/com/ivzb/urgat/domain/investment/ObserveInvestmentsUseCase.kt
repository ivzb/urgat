package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface ObserveInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<String>? = null,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Investment>>>

}

class DefaultObserveInvestmentsUseCase @Inject constructor(
    repository: InvestmentsRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveInvestmentsUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Investment>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
