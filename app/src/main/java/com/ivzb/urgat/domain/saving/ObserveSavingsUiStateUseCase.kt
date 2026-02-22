package com.ivzb.urgat.domain.saving

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.savings.SavingsFilteringUiInfo
import com.ivzb.urgat.ui.savings.SavingsSortType
import com.ivzb.urgat.ui.savings.SavingsUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveSavingsUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<SavingsSortType>,
    ): Flow<Async<SavingsUiState>>

}

class DefaultObserveSavingsUiStateUseCase @Inject constructor(
    private val observeGroupedSavingsMonthlyUseCase: ObserveGroupedSavingsMonthlyUseCase,
    private val observeGroupedSavingCategoriesMonthlyUseCase: ObserveGroupedSavingCategoriesMonthlyUseCase,
    private val observeGroupedSavingCategoriesYearlyUseCase: ObserveGroupedSavingCategoriesYearlyUseCase,
    private val observeGroupedSavingCategoriesAllTimeUseCase: ObserveGroupedSavingCategoriesAllTimeUseCase,
    private val observeSavingsSortingUseCase: ObserveSavingsSortingUseCase,
): ObserveSavingsUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<SavingsSortType>,
    ): Flow<Async<SavingsUiState>> = combineUiState(
        observeGroupedSavingsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedSavingCategoriesMonthlyUseCase(searchQueryFlow, dateRangeFlow = null, null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedSavingCategoriesYearlyUseCase(searchQueryFlow, dateRangeFlow = null, null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedSavingCategoriesAllTimeUseCase(searchQueryFlow, dateRangeFlow = null, null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeSavingsSortingUseCase(),
        combine(searchQueryFlow, sortTypeFlow, ::Pair).map { Async.Success(it) },

        onLoading = {
            SavingsUiState(isLoading = true)
        },
        onError = {
            SavingsUiState(hasError = true)
        },
        onSuccess = { savings, categoriesMonthly, categoriesYearly, categoriesAllTime, sorting, (searchQuery, sortType) ->
            SavingsUiState(
                savings = savings,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                filteringUiInfo = SavingsFilteringUiInfo(
                    currentFilteringLabel = R.string.all_savings,
                    emptyLabel = if (searchQuery.isEmpty()) R.string.no_savings_label else R.string.no_savings_search,
                    emptyMessage = R.string.no_savings_message,
                    emptyButtonLabel = R.string.add_saving,

                    sortMenuItems = sorting,
                    showSortBadge = sortType != SavingsSortType.BY_DATE,
                ),
                isLoading = false,
            )
        }
    ).map { Async.Success(it) }

}