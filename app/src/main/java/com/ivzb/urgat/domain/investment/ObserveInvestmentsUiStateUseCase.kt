package com.ivzb.urgat.domain.investment

import com.ivzb.urgat.ui.investments.InvestmentsFilteringUiInfo
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
                filteringUiInfo = InvestmentsFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }
    
}

