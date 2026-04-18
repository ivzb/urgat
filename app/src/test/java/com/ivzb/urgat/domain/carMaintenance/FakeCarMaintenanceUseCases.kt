package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceUiState
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FakeObserveCarMaintenanceUiStateUseCase : ObserveCarMaintenanceUiStateUseCase {

    private val flow = MutableStateFlow<Async<CarMaintenanceUiState>>(Async.Loading)

    var lastQuery: String = ""
    var lastFilterType: String = CarMaintenanceFilterType_All

    fun emit(value: Async<CarMaintenanceUiState>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<CarMaintenanceSortType>,
    ): Flow<Async<CarMaintenanceUiState>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        CoroutineScope(Dispatchers.Unconfined).launch {
            filterTypeFlow.collect { lastFilterType = it }
        }
        return flow
    }

}

