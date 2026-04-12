package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionUiState
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FakeObserveFuelConsumptionUiStateUseCase : ObserveFuelConsumptionUiStateUseCase {

    private val flow = MutableStateFlow<Async<FuelConsumptionUiState>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<FuelConsumptionUiState>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        sortTypeFlow: Flow<FuelConsumptionSortType>,
    ): Flow<Async<FuelConsumptionUiState>> {
        // Record query changes for assertions
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveFuelConsumptionUseCase : ObserveFuelConsumptionUseCase {

    private val flow = MutableStateFlow<Async<List<FuelConsumption>>>(Async.Loading)

    fun emit(value: Async<List<FuelConsumption>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<FuelConsumption>>> {
        return flow
    }

}

class FakeObserveGroupedFuelConsumptionMonthlyUseCase : ObserveGroupedFuelConsumptionMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedFuelConsumption>>>(Async.Loading)

    fun emit(value: Async<List<GroupedFuelConsumption>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumption>>> {
        return flow
    }

}

class FakeObserveFuelConsumptionSortingUseCase : ObserveFuelConsumptionSortingUseCase {

    private val flow = MutableStateFlow<Async<List<FuelConsumptionSortMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<FuelConsumptionSortMenuItem>>) {
        flow.value = value
    }

    override fun invoke(): Flow<Async<List<FuelConsumptionSortMenuItem>>> {
        return flow
    }

}

class FakeCreateFuelConsumptionUseCase : CreateFuelConsumptionUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        created = listOf(amount, price, currency, carName, odometer, date)
    }
}


