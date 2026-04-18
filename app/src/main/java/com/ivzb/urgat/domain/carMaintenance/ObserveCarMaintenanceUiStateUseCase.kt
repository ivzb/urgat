package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilteringUiInfo
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveCarMaintenanceUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<CarMaintenanceSortType>,
    ): Flow<Async<CarMaintenanceUiState>>

}

class DefaultObserveCarMaintenanceUiStateUseCase @Inject constructor(
    private val observeGroupedCarMaintenanceMonthlyUseCase: ObserveGroupedCarMaintenanceMonthlyUseCase,
    private val observeCarMaintenanceFilteringUseCase: ObserveCarMaintenanceFilteringUseCase,
    private val observeCarMaintenanceSortingUseCase: ObserveCarMaintenanceSortingUseCase,
): ObserveCarMaintenanceUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<CarMaintenanceSortType>,
    ): Flow<Async<CarMaintenanceUiState>> = combineUiState(
        observeGroupedCarMaintenanceMonthlyUseCase(searchQueryFlow = searchQueryFlow, dateRangeFlow = null, filterTypeFlow = filterTypeFlow, sortTypeFlow = sortTypeFlow, limitFlow = null),
        observeCarMaintenanceFilteringUseCase(),
        observeCarMaintenanceSortingUseCase(),
        combine(searchQueryFlow, filterTypeFlow, sortTypeFlow, ::Triple).map { Async.Success(it) },

        onLoading = {
            CarMaintenanceUiState(isLoading = true)
        },
        onError = {
            CarMaintenanceUiState(hasError = true)
        },
        onSuccess = { carMaintenance, filtering, sorting, (searchQuery, filterType, sortType) ->
            CarMaintenanceUiState(
                carMaintenance = carMaintenance,
                isLoading = false,
                filteringUiInfo = CarMaintenanceFilteringUiInfo.from(
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

