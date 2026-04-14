package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilteringUiInfo
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveFuelConsumptionUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<FuelConsumptionSortType>,
    ): Flow<Async<FuelConsumptionUiState>>

}

class DefaultObserveFuelConsumptionUiStateUseCase @Inject constructor(
    private val observeGroupedFuelConsumptionMonthlyUseCase: ObserveGroupedFuelConsumptionMonthlyUseCase,
    private val observeGroupedFuelConsumptionCategoriesMonthlyUseCase: ObserveGroupedFuelConsumptionCategoriesMonthlyUseCase,
    private val observeGroupedFuelConsumptionCategoriesYearlyUseCase: ObserveGroupedFuelConsumptionCategoriesYearlyUseCase,
    private val observeGroupedFuelConsumptionCategoriesAllTimeUseCase: ObserveGroupedFuelConsumptionCategoriesAllTimeUseCase,
    private val observeFuelConsumptionFilteringUseCase: ObserveFuelConsumptionFilteringUseCase,
    private val observeFuelConsumptionSortingUseCase: ObserveFuelConsumptionSortingUseCase,
): ObserveFuelConsumptionUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<FuelConsumptionSortType>,
    ): Flow<Async<FuelConsumptionUiState>> = combineUiState(
        observeGroupedFuelConsumptionMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedFuelConsumptionCategoriesMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedFuelConsumptionCategoriesYearlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeGroupedFuelConsumptionCategoriesAllTimeUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeFuelConsumptionFilteringUseCase(),
        observeFuelConsumptionSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            FuelConsumptionUiState(isLoading = true)
        },
        onError = {
            FuelConsumptionUiState(hasError = true)
        },
        onSuccess = { fuelConsumption, categoriesMonthly, categoriesYearly, categoriesAllTime, filtering, sorting, (searchQuery, filterType, sortType) ->
            FuelConsumptionUiState(
                fuelConsumption = fuelConsumption,
                categoriesMonthly = categoriesMonthly,
                categoriesYearly = categoriesYearly,
                categoriesAllTime = categoriesAllTime,
                isLoading = false,
                filteringUiInfo = FuelConsumptionFilteringUiInfo.from(
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

