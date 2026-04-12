package com.ivzb.urgat.ui.car

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.car.CarRepository
import com.ivzb.urgat.domain.fuelConsumption.ObserveFuelConsumptionUiStateUseCase
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarViewModel @Inject constructor(
    observeFuelConsumptionUiStateUseCase: ObserveFuelConsumptionUiStateUseCase,
    private val carRepository: CarRepository,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<CarUiEvent>()

    private val _searchQuery = carRepository.searchQueryFlow

    private val _savedFuelConsumptionSortType = carRepository.fuelConsumptionSortTypeFlow

    val uiState: StateFlow<CarUiState> = combineUiState(
        observeFuelConsumptionUiStateUseCase(_searchQuery, _savedFuelConsumptionSortType),
        _searchQuery.map { Async.Success(it) },
        uiEventHolder(),

        onLoading = {
            CarUiState(isLoading = true)
        },
        onError = {
            CarUiState(hasError = true)
        },
        onSuccess = { consumptionUiState, searchQuery, uiEvent ->
            CarUiState(
                fuelConsumptionUiState = consumptionUiState,
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
            is CarIntent.SetFuelConsumptionSorting -> setFuelConsumptionSorting(intent.sortType)
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
                    else -> null
                }
            )
        }
    }

    private fun setFuelConsumptionSorting(sortType: FuelConsumptionSortType) {
        viewModelScope.launch {
            carRepository.updateFuelConsumptionSortType(sortType)
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setFuelConsumptionSorting(FuelConsumptionSortType.BY_DATE)
    }

}