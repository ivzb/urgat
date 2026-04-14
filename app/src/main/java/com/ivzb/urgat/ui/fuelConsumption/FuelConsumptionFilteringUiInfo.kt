package com.ivzb.urgat.ui.fuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.expenses.ExpensesFilterMenuItem
import com.ivzb.urgat.ui.expenses.ExpensesFilterType
import com.ivzb.urgat.ui.investments.InvestmentsFilterType_All

data class FuelConsumptionFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.transactions,

    override val emptyLabel: Int = R.string.no_fuel_consumption_label,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_expenses,
    override val emptyMessage: Int = R.string.no_fuel_consumption_message,
    override val emptyButtonLabel: Int = R.string.add_fuel_consumption,

    override val loadingLabel: Int = R.string.loading_fuel_consumption,

    override val errorLabel: Int = R.string.error_fuel_consumption,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val filterMenuItems: List<FuelConsumptionFilterMenuItem> = listOf(),
    val showFilterBadge: Boolean = false,

    val sortMenuItems: List<FuelConsumptionSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            filterType: String,
            filterMenuItems: List<FuelConsumptionFilterMenuItem>,
            sortType: FuelConsumptionSortType,
            sortMenuItems: List<FuelConsumptionSortMenuItem>,
        ): FuelConsumptionFilteringUiInfo {
            val showFilterBadge = filterType != InvestmentsFilterType_All
            val showSortBadge = sortType != FuelConsumptionSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

            return FuelConsumptionFilteringUiInfo(
                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_fuel_consumption_search
                    else -> R.string.no_fuel_consumption
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_fuel_consumption_search_message
                    else -> R.string.no_fuel_consumption_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_expense
                },

                searchQuery = searchQuery,
                hasAppliedFilters = hasAppliedFilters,

                filterMenuItems = filterMenuItems,
                showFilterBadge = showFilterBadge,

                sortMenuItems = sortMenuItems,
                showSortBadge = showSortBadge,
            )
        }
    }
}