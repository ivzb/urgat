package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.investments.InvestmentsFilterMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All
import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
import com.ivzb.urgat.ui.investments.InvestmentsSortMenuItem
import com.ivzb.urgat.ui.investments.InvestmentsSortType
import com.ivzb.urgat.ui.investments.InvestmentsUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveInvestmentsUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<InvestmentsSortType>,
    ): Flow<Async<InvestmentsUiState>>

}

class DefaultObserveInvestmentsUiStateUseCase @Inject constructor(
    private val observeGroupedInvestmentsMonthlyUseCase: ObserveGroupedInvestmentsMonthlyUseCase,
    private val observeGroupedInvestmentCategoriesYearlyUseCase: ObserveGroupedInvestmentCategoriesYearlyUseCase,
    private val observeInvestmentAssetsUseCase: ObserveInvestmentAssetsUseCase,
    private val observeInvestmentCategoriesUseCase: ObserveInvestmentCategoriesUseCase,
    private val observeInvestmentNetWorthUseCase: ObserveInvestmentNetWorthUseCase,
    private val observeInvestmentsFilteringUseCase: ObserveInvestmentsFilteringUseCase,
    private val observeInvestmentsSortingUseCase: ObserveInvestmentsSortingUseCase,
): ObserveInvestmentsUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<InvestmentsSortType>,
    ): Flow<Async<InvestmentsUiState>> = combineUiState(
        observeGroupedInvestmentsMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedInvestmentCategoriesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeInvestmentAssetsUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeInvestmentCategoriesUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeInvestmentNetWorthUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeInvestmentsFilteringUseCase(),
        observeInvestmentsSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            InvestmentsUiState(isLoading = true)
        },
        onError = {
            InvestmentsUiState(hasError = true)
        },
        onSuccess = { investments, categoriesYearly, assets, categories, netWorth, filtering, sorting, (searchQuery, filterType, sortType) ->
            InvestmentsUiState(
                investments = investments,
                categoriesYearly = categoriesYearly,
                assets = assets,
                categories = categories,
                netWorth = netWorth,
                filteringUiInfo = getFilterUiInfo(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }
    
    private fun getFilterUiInfo(
        searchQuery: String,
        filterType: String,
        filterMenuItems: List<InvestmentsFilterMenuItem>,
        sortType: InvestmentsSortType,
        sortMenuItems: List<InvestmentsSortMenuItem>,
    ): InvestmentsFilteringUiInfo {
        val showFilterBadge = filterType != InvestmentsFilterType_All
        val showSortBadge = sortType != InvestmentsSortType.BY_DATE
        val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

        return InvestmentsFilteringUiInfo(
            currentFilteringLabel = R.string.all_investments,

            emptyLabel = when {
                hasAppliedFilters -> R.string.no_investments_search
                else -> R.string.no_investments
            },
            emptyMessage = when {
                hasAppliedFilters -> R.string.no_investments_search_message
                else -> R.string.no_investments_message
            },
            emptyButtonLabel = when {
                hasAppliedFilters -> R.string.reset_filters
                else -> R.string.add_investment
            },

            searchQuery = searchQuery,
            hasAppliedFilters = hasAppliedFilters,

            filterMenuItems = filterMenuItems,
            showFilterBadge = showFilterBadge,

            sortMenuItems = sortMenuItems,
            showSortBadge = showSortBadge,
        )
    }

}