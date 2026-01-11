package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        filterTypeFlow: Flow<String>? = null,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Investment>>>

}

class DefaultObserveInvestmentsUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveInvestmentsUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Investment>>> = observeUseCase(searchQueryFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}