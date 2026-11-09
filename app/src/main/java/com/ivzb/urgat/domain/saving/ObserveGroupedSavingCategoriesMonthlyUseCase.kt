package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.data.saving.SavingTotal
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.domain.common.Group.groupSavingCategoriesMonthly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

typealias GroupedSavingCategories = GroupedItems<String, List<SavingTotal>, SavingCategory>

interface ObserveGroupedSavingCategoriesMonthlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedSavingCategories>>>

}

class DefaultObserveGroupedSavingCategoriesMonthlyUseCase @Inject constructor(
    savingsRepository: SavingsRepository,
    private val randomRepository: RandomRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
): ObserveGroupedSavingCategoriesMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = savingsRepository,
        dispatcher = dispatcher,
        filter = ::filterSavings,
        sort = ::sortSavings,
        group = { savings -> groupSavingCategoriesMonthly(savings, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedSavingCategories>>> = observeGroupedUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
