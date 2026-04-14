package com.ivzb.urgat.ui.car

import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.finance.FinanceIntent
import com.ivzb.urgat.ui.fuelConsumption.FuelConsumptionSortType

sealed class CarIntent {
    data class SearchQueryChanged(val query: String): CarIntent()
    data class FabActionClick(val page: Page): CarIntent()

    data class SetFuelConsumptionFiltering(val filterType: String): CarIntent()
    data class SetFuelConsumptionSorting(val sortType: FuelConsumptionSortType): CarIntent()

    object ResetFilters: CarIntent()
    object EventConsumed: CarIntent()
}
