package com.ivzb.urgat.ui.fuelConsumption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.car.CarRepository
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FuelConsumptionViewModel @Inject constructor(
    observeFuelConsumptionUiStateUseCase: ObserveFuelConsumptionUiStateUseCase,
    private val carRepository: CarRepository,
) : ViewModel() {

    private val savedFilterFlow = combine(
        carRepository.fuelConsumptionFilterTypeFlow,
        carRepository.fuelConsumptionCarFilterFlow,
        ::FuelConsumptionFilter,
    )
    private val savedSortTypeFlow = carRepository.fuelConsumptionSortTypeFlow
    private val searchQueryFlow = carRepository.searchQueryFlow

    val uiState: StateFlow<FuelConsumptionUiState> = combineUiState(
        observeFuelConsumptionUiStateUseCase(searchQueryFlow, savedFilterFlow, savedSortTypeFlow),

        onLoading = { FuelConsumptionUiState(isLoading = true) },
        onError = { FuelConsumptionUiState(hasError = true) },
        onSuccess = { fuelConsumptionUiState -> fuelConsumptionUiState },
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = FuelConsumptionUiState(isLoading = true)
        )

    fun onIntent(intent: FuelConsumptionIntent) {
        when (intent) {
            is FuelConsumptionIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is FuelConsumptionIntent.SetFiltering -> setFiltering(intent.filterType)
            is FuelConsumptionIntent.SetCarFiltering -> setCarFiltering(intent.carName)
            is FuelConsumptionIntent.SetSorting -> setSorting(intent.sortType)
            is FuelConsumptionIntent.ResetFilters -> resetFilters()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            carRepository.updateSearchQuery(newQuery)
        }
    }

    private fun setFiltering(filterType: String) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionFilterType(filterType)
        }
    }

    private fun setCarFiltering(carName: String) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionCarFilter(carName)
        }
    }

    private fun setSorting(sortType: FuelConsumptionSortType) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionSortType(sortType)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setFiltering(FUEL_CONSUMPTION_FILTER_TYPE_ALL)
        setCarFiltering(FUEL_CONSUMPTION_FILTER_TYPE_ALL)
        setSorting(FuelConsumptionSortType.BY_DATE)
    }
}
