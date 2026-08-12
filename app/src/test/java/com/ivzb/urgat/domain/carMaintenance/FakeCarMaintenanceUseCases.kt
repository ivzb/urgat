package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterMenuItem
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceFilterType_All
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortMenuItem
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

class FakeObserveGroupedCarMaintenanceMonthlyUseCase : ObserveGroupedCarMaintenanceMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedCarMaintenance>>>(Async.Loading)

    var lastQuery: String = ""
    var lastFilterType: String = CarMaintenanceFilterType_All

    fun emit(value: Async<List<GroupedCarMaintenance>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<CarMaintenanceSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedCarMaintenance>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        CoroutineScope(Dispatchers.Unconfined).launch {
            filterTypeFlow?.collect { lastFilterType = it }
        }
        return flow
    }

}

class FakeObserveCarMaintenanceFilteringUseCase : ObserveCarMaintenanceFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<CarMaintenanceFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<CarMaintenanceFilterMenuItem>>) {
        flow.value = value
    }

    override fun invoke(): Flow<Async<List<CarMaintenanceFilterMenuItem>>> {
        return flow
    }

}

class FakeObserveCarMaintenanceSortingUseCase : ObserveCarMaintenanceSortingUseCase {

    private val flow = MutableStateFlow<Async<List<CarMaintenanceSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<CarMaintenanceSortMenuItem>>) {
        flow.value = value
    }

    override fun invoke(): Flow<Async<List<CarMaintenanceSortMenuItem>>> {
        return flow
    }

}

class FakeGetCarMaintenanceUseCase : GetCarMaintenanceUseCase {

    var toReturn: CarMaintenance? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): CarMaintenance? {
        calledWithId = id
        return toReturn
    }

}

class FakeGetLastUsedCarMaintenanceCarNameUseCase : GetLastUsedCarMaintenanceCarNameUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }

}

class FakeCreateCarMaintenanceUseCase : CreateCarMaintenanceUseCase {

    var created: List<Any?> = listOf()

    override suspend fun invoke(
        description: String,
        partNumber: String?,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        created = listOf(description, partNumber, url, carName, odometer, date)
    }

}

class FakeUpdateCarMaintenanceUseCase : UpdateCarMaintenanceUseCase {

    var updated: List<Any?> = listOf()

    override suspend fun invoke(
        id: String,
        description: String,
        partNumber: String,
        url: String?,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        updated = listOf(id, description, partNumber, url, carName, odometer, date)
    }

}

class FakeDeleteCarMaintenanceUseCase : DeleteCarMaintenanceUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(id: String) {
        deleted = listOf(id)
    }

}

