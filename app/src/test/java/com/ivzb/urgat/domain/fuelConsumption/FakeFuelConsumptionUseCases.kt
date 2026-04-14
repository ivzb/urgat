package com.ivzb.urgat.domain.fuelConsumption

import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterMenuItem
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionFilterType_All
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
    var lastFilterType: String = FuelConsumptionFilterType_All

    fun emit(value: Async<FuelConsumptionUiState>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<String>,
        sortTypeFlow: Flow<FuelConsumptionSortType>,
    ): Flow<Async<FuelConsumptionUiState>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        CoroutineScope(Dispatchers.Unconfined).launch {
            filterTypeFlow.collect { lastFilterType = it }
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

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedFuelConsumption>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumption>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow?.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedFuelConsumptionCategoriesMonthlyUseCase : ObserveGroupedFuelConsumptionCategoriesMonthlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedFuelConsumptionCategories>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedFuelConsumptionCategories>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumptionCategories>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedFuelConsumptionCategoriesYearlyUseCase : ObserveGroupedFuelConsumptionCategoriesYearlyUseCase {

    private val flow = MutableStateFlow<Async<List<GroupedFuelConsumptionCategories>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<GroupedFuelConsumptionCategories>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<GroupedFuelConsumptionCategories>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveGroupedFuelConsumptionCategoriesAllTimeUseCase : ObserveGroupedFuelConsumptionCategoriesAllTimeUseCase {

    private val flow = MutableStateFlow<Async<List<FuelConsumptionCategory>>>(Async.Loading)

    var lastQuery: String = ""

    fun emit(value: Async<List<FuelConsumptionCategory>>) {
        flow.value = value
    }

    override fun invoke(
        searchQueryFlow: Flow<String>,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<String>?,
        sortTypeFlow: Flow<FuelConsumptionSortType>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<FuelConsumptionCategory>>> {
        CoroutineScope(Dispatchers.Unconfined).launch {
            searchQueryFlow.collect { lastQuery = it }
        }
        return flow
    }

}

class FakeObserveFuelConsumptionFilteringUseCase : ObserveFuelConsumptionFilteringUseCase {

    private val flow = MutableStateFlow<Async<List<FuelConsumptionFilterMenuItem>>>(Async.Loading)

    fun emit(value: Async<List<FuelConsumptionFilterMenuItem>>) {
        flow.value = value
    }

    override fun invoke(): Flow<Async<List<FuelConsumptionFilterMenuItem>>> {
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

class FakeGetFuelConsumptionUseCase : GetFuelConsumptionUseCase {

    var toReturn: FuelConsumption? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): FuelConsumption? {
        calledWithId = id
        return toReturn
    }
}

class FakeGetLastUsedFuelConsumptionCurrencyUseCase : GetLastUsedFuelConsumptionCurrencyUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }
}

class FakeGetLastUsedFuelConsumptionTypeUseCase : GetLastUsedFuelConsumptionTypeUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }
}

class FakeGetLastUsedFuelConsumptionCarNameUseCase : GetLastUsedFuelConsumptionCarNameUseCase {

    var toReturn: String? = null

    override suspend fun invoke(): String? {
        return toReturn
    }
}

class FakeCreateFuelConsumptionUseCase : CreateFuelConsumptionUseCase {

    var created: List<Any> = listOf()

    override suspend fun invoke(
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        type: String,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        created = listOf(amount, price, currency, type, carName, odometer, date)
    }
}

class FakeUpdateFuelConsumptionUseCase : UpdateFuelConsumptionUseCase {

    var updated: List<Any> = listOf()

    override suspend fun invoke(
        id: String,
        amount: BigDecimal,
        price: BigDecimal,
        currency: String,
        type: String,
        carName: String,
        odometer: Int,
        date: Long,
    ) {
        updated = listOf(id, amount, price, currency, type, carName, odometer, date)
    }
}

class FakeDeleteFuelConsumptionUseCase : DeleteFuelConsumptionUseCase {

    var deleted: List<Any> = listOf()

    override suspend fun invoke(id: String) {
        deleted = listOf(id)
    }
}
