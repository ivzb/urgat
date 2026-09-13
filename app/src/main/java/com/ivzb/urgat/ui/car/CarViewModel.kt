package com.ivzb.urgat.ui.car

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.car.CarRepository
import com.ivzb.urgat.domain.carMaintenance.ObserveCarMaintenanceUiStateUseCase
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilter
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarViewModel @Inject constructor(
    observeFuelConsumptionUiStateUseCase: ObserveFuelConsumptionUiStateUseCase,
    observeCarMaintenanceUiStateUseCase: ObserveCarMaintenanceUiStateUseCase,
    private val carRepository: CarRepository,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<CarUiEvent>()

    private val _searchQuery = carRepository.searchQueryFlow

    private val _savedFuelConsumptionFilter = combine(
        carRepository.fuelConsumptionFilterTypeFlow,
        carRepository.fuelConsumptionCarFilterFlow,
        ::FuelConsumptionFilter,
    )
    private val _savedFuelConsumptionSortType = carRepository.fuelConsumptionSortTypeFlow

    private val _savedCarMaintenanceFilterType = carRepository.carMaintenanceFilterTypeFlow
    private val _savedCarMaintenanceSortType = carRepository.carMaintenanceSortTypeFlow

    val uiState: StateFlow<CarUiState> = combineUiState(
        observeFuelConsumptionUiStateUseCase(_searchQuery, _savedFuelConsumptionFilter, _savedFuelConsumptionSortType),
        observeCarMaintenanceUiStateUseCase(_searchQuery, _savedCarMaintenanceFilterType, _savedCarMaintenanceSortType),
        _searchQuery.map { Async.Success(it) },
        uiEventHolder(),

        onLoading = {
            CarUiState(isLoading = true)
        },
        onError = {
            CarUiState(hasError = true)
        },
        onSuccess = { consumptionUiState, maintenanceUiState, searchQuery, uiEvent ->
            CarUiState(
                fuelConsumptionUiState = consumptionUiState,
                carMaintenanceUiState = maintenanceUiState,
                isLoading = false,
                filteringUiInfo = CarFilteringUiInfo(
                    searchQuery = searchQuery,
                ),
                uiEvent = uiEvent,
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = CarUiState(isLoading = true)
    )

    fun onIntent(intent: CarIntent) {
        when (intent) {
            is CarIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is CarIntent.ResetFilters -> resetFilters()
            is CarIntent.FabActionClick -> onFabActionClick(intent.page)

            is CarIntent.SetFuelConsumptionFiltering -> setFuelConsumptionFiltering(intent.filterType)
            is CarIntent.SetFuelConsumptionCarFiltering -> setFuelConsumptionCarFiltering(intent.carName)
            is CarIntent.SetFuelConsumptionSorting -> setFuelConsumptionSorting(intent.sortType)

            is CarIntent.SetCarMaintenanceFiltering -> setCarMaintenanceFiltering(intent.filterType)
            is CarIntent.SetCarMaintenanceSorting -> setCarMaintenanceSorting(intent.sortType)

            is CarIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            carRepository.updateSearchQuery(newQuery)
        }
    }

    private fun onFabActionClick(page: Page) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (page) {
                    CarPage.FuelConsumption -> CarUiEvent.AddFuelConsumption
                    CarPage.Maintenance -> CarUiEvent.AddCarMaintenance
                    else -> null
                }
            )
        }
    }

    private fun setFuelConsumptionFiltering(filterType: String) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionFilterType(filterType)
        }
    }

    private fun setFuelConsumptionCarFiltering(carName: String) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionCarFilter(carName)
        }
    }

    private fun setFuelConsumptionSorting(sortType: FuelConsumptionSortType) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionSortType(sortType)
        }
    }

    private fun setCarMaintenanceFiltering(filterType: String) {
        viewModelScope.launch {
            carRepository.updateCarMaintenanceFilterType(filterType)
        }
    }

    private fun setCarMaintenanceSorting(sortType: CarMaintenanceSortType) {
        viewModelScope.launch {
            carRepository.updateCarMaintenanceSortType(sortType)
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setFuelConsumptionFiltering(FuelConsumptionFilterType_All)
        setFuelConsumptionCarFiltering(FuelConsumptionFilterType_All)
        setFuelConsumptionSorting(FuelConsumptionSortType.BY_DATE)
        setCarMaintenanceFiltering(CarMaintenanceFilterType_All)
        setCarMaintenanceSorting(CarMaintenanceSortType.BY_DATE)
    }

}