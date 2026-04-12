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
        sortTypeFlow: Flow<FuelConsumptionSortType>,
    ): Flow<Async<FuelConsumptionUiState>>

}

class DefaultObserveFuelConsumptionUiStateUseCase @Inject constructor(
    private val observeGroupedFuelConsumptionMonthlyUseCase: ObserveGroupedFuelConsumptionMonthlyUseCase,
    private val observeFuelConsumptionSortingUseCase: ObserveFuelConsumptionSortingUseCase,
): ObserveFuelConsumptionUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<FuelConsumptionSortType>,
    ): Flow<Async<FuelConsumptionUiState>> = combineUiState(
        observeGroupedFuelConsumptionMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeFuelConsumptionSortingUseCase(),
        combine(searchQueryFlow, sortTypeFlow, ::Pair).map { Async.Success(it) },

        onLoading = {
            FuelConsumptionUiState(isLoading = true)
        },
        onError = {
            FuelConsumptionUiState(hasError = true)
        },
        onSuccess = { fuelConsumption, sorting, (searchQuery, sortType) ->
            FuelConsumptionUiState(
                fuelConsumption = fuelConsumption,
                isLoading = false,
                filteringUiInfo = FuelConsumptionFilteringUiInfo.from(
                    searchQuery = searchQuery,
                    sortType = sortType,
                    sortMenuItems = sorting,
                ),
            )
        }
    ).map { Async.Success(it) }

}

