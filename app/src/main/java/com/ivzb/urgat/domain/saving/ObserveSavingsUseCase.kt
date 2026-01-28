package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingsRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveSavingsUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Saving>>>

}

class DefaultObserveSavingsUseCase @Inject constructor(
    repository: SavingsRepository
): ObserveSavingsUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        filter = ::filterSavings,
        sort = ::sortSavings,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<SavingsSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Saving>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}