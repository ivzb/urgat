package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupInvestmentCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias GroupedInvestmentCategories = GroupedItems<String, String, InvestmentCategory>

interface ObserveGroupedInvestmentCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestmentCategories>>>

}

class DefaultObserveGroupedInvestmentCategoriesMonthlyUseCase @Inject constructor(
    investmentsRepository: InvestmentsRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedInvestmentCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = investmentsRepository,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
        group = { investments -> groupInvestmentCategoriesMonthly(investments, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestmentCategories>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
