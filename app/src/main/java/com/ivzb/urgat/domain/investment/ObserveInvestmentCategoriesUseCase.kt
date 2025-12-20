package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.common.Group.groupInvestmentCategories
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveInvestmentCategoriesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<InvestmentCategory>>>

}

class DefaultObserveInvestmentCategoriesUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveInvestmentCategoriesUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterInvestments,
        group = ::groupInvestmentCategories,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<InvestmentCategory>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}
