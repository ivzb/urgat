package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

typealias GroupedSavings = GroupedItems<String, List<SavingTotal>, Saving>

interface ObserveGroupedSavingsMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedSavings>>>

}

class DefaultObserveGroupedSavingsMonthlyUseCase @Inject constructor(
    repository: SavingsRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedSavingsMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
        dispatcher = dispatcher,
        filter = ::filterSavings,
        sort = ::sortSavings,
        group = { items -> Group.byDateMonthly(items, Saving::date, Saving::total) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedSavings>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
