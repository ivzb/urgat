package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.domain.common.ObserveGroupedUseCase
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

typealias GroupedSavings = GroupedItems<String, BigDecimal, Saving>

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
    repository: SavingsRepository
): ObserveGroupedSavingsMonthlyUseCase {

    private val observeGroupedUseCase = ObserveGroupedUseCase(
        repository = repository,
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