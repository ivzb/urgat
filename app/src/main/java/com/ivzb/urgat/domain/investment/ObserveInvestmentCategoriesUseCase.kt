package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupInvestmentCategories
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ObserveInvestmentCategoriesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<InvestmentCategory>>>

}

class DefaultObserveInvestmentCategoriesUseCase @Inject constructor(
    investmentsRepository: InvestmentsRepository,
    private val randomRepository: RandomRepository,
): ObserveInvestmentCategoriesUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = investmentsRepository,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
        group = { investments -> groupInvestmentCategories(investments, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<InvestmentCategory>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
