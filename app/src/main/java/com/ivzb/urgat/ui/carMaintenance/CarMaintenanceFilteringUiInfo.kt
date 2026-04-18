package com.ivzb.urgat.ui.carMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class CarMaintenanceFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.transactions,

    override val emptyLabel: Int = R.string.no_car_maintenance_label,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_expenses,
    override val emptyMessage: Int = R.string.no_car_maintenance_message,
    override val emptyButtonLabel: Int = R.string.add_car_maintenance,

    override val loadingLabel: Int = R.string.loading_car_maintenance,

    override val errorLabel: Int = R.string.error_car_maintenance,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
    override val hasAppliedFilters: Boolean = false,

    val filterMenuItems: List<CarMaintenanceFilterMenuItem> = listOf(),
    val showFilterBadge: Boolean = false,

    val sortMenuItems: List<CarMaintenanceSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    companion object {

        fun from(
            searchQuery: String,
            filterType: String,
            filterMenuItems: List<CarMaintenanceFilterMenuItem>,
            sortType: CarMaintenanceSortType,
            sortMenuItems: List<CarMaintenanceSortMenuItem>,
        ): CarMaintenanceFilteringUiInfo {
            val showFilterBadge = filterType != CarMaintenanceFilterType_All
            val showSortBadge = sortType != CarMaintenanceSortType.BY_DATE
            val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge || showSortBadge

            return CarMaintenanceFilteringUiInfo(
                emptyLabel = when {
                    hasAppliedFilters -> R.string.no_car_maintenance_search
                    else -> R.string.no_car_maintenance
                },
                emptyMessage = when {
                    hasAppliedFilters -> R.string.no_car_maintenance_search_message
                    else -> R.string.no_car_maintenance_message
                },
                emptyButtonLabel = when {
                    hasAppliedFilters -> R.string.reset_filters
                    else -> R.string.add_car_maintenance
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

