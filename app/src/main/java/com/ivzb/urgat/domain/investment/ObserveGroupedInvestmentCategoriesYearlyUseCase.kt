package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentTotal
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.GroupInvestments.groupInvestmentCategoriesYearly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

typealias GroupedInvestmentCategories = GroupedItems<String, List<InvestmentTotal>, InvestmentCategory>

interface ObserveGroupedInvestmentCategoriesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestmentCategories>>>

}

class DefaultObserveGroupedInvestmentCategoriesYearlyUseCase @Inject constructor(
    investmentsRepository: InvestmentsRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedInvestmentCategoriesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = investmentsRepository,
        dispatcher = dispatcher,
        filter = ::filterInvestments,
        sort = ::sortInvestments,
        transform = ::excludeClosedPositions,
        group = { investments -> groupInvestmentCategoriesYearly(investments, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<InvestmentsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedInvestmentCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
