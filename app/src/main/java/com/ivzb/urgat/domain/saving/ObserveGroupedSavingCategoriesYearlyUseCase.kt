package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.data.random.RandomRepository
import com.ivzb.urgat.domain.common.Group.groupSavingCategoriesYearly
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveGroupedSavingCategoriesYearlyUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedSavingCategories>>>

}

class DefaultObserveGroupedSavingCategoriesYearlyUseCase @Inject constructor(
    savingsRepository: SavingsRepository,
    private val randomRepository: RandomRepository,
): ObserveGroupedSavingCategoriesYearlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = savingsRepository,
        filter = ::filterSavings,
        sort = ::sortSavings,
        group = { savings -> groupSavingCategoriesYearly(savings, randomRepository::uuid) },
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedSavingCategories>>> = observeGroupedUseCase(searchQueryFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
