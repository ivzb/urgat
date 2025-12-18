package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentsRepository
import com.ivzb.urgat.domain.default.Group
import com.ivzb.urgat.domain.default.ObserveGroupedUseCase
import com.ivzb.urgat.ui.GroupedItems
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedInvestmentsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedItems<String, Investment>>>>

}

class DefaultObserveGroupedInvestmentsUseCase @Inject constructor(
    repository: InvestmentsRepository
): ObserveGroupedInvestmentsUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        filter = ::filterInvestments,
        group = { items -> Group.byDate(items, Investment::date) },
        errorMessageResId = R.string.error_investments,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedItems<String, Investment>>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}