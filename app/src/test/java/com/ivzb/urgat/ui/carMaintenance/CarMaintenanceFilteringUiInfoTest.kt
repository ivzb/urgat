package com.ivzb.urgat.ui.carMaintenance

import com.ivzb.urgat.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CarMaintenanceFilteringUiInfoTest {

    @Test
    fun `from with default values has no applied filters`() {
        val info = CarMaintenanceFilteringUiInfo.from(
            searchQuery = "",
            filterType = CarMaintenanceFilterType_All,
            filterMenuItems = listOf(),
            sortType = CarMaintenanceSortType.BY_DATE,
            sortMenuItems = listOf(),
        )

        assertFalse(info.hasAppliedFilters)
        assertFalse(info.showFilterBadge)
        assertFalse(info.showSortBadge)
    }

    @Test
    fun `from with search query has applied filters`() {
        val info = CarMaintenanceFilteringUiInfo.from(
            searchQuery = "oil",
            filterType = CarMaintenanceFilterType_All,
            filterMenuItems = listOf(),
            sortType = CarMaintenanceSortType.BY_DATE,
            sortMenuItems = listOf(),
        )

        assertTrue(info.hasAppliedFilters)
        assertEquals("oil", info.searchQuery)
    }

    @Test
    fun `from with non-default filter type shows filter badge`() {
        val info = CarMaintenanceFilteringUiInfo.from(
            searchQuery = "",
            filterType = "Car A",
            filterMenuItems = listOf(),
            sortType = CarMaintenanceSortType.BY_DATE,
            sortMenuItems = listOf(),
        )

        assertTrue(info.hasAppliedFilters)
        assertTrue(info.showFilterBadge)
    }

    @Test
    fun `from with non-default sort type shows sort badge`() {
        val info = CarMaintenanceFilteringUiInfo.from(
            searchQuery = "",
            filterType = CarMaintenanceFilterType_All,
            filterMenuItems = listOf(),
            sortType = CarMaintenanceSortType.BY_ODOMETER_MIN,
            sortMenuItems = listOf(),
        )

        assertTrue(info.hasAppliedFilters)
        assertTrue(info.showSortBadge)
    }

    @Test
    fun `from with applied filters shows search empty label`() {
        val info = CarMaintenanceFilteringUiInfo.from(
            searchQuery = "query",
            filterType = CarMaintenanceFilterType_All,
            filterMenuItems = listOf(),
            sortType = CarMaintenanceSortType.BY_DATE,
            sortMenuItems = listOf(),
        )

        assertEquals(R.string.no_car_maintenance_search, info.emptyLabel)
        assertEquals(R.string.no_car_maintenance_search_message, info.emptyMessage)
        assertEquals(R.string.reset_filters, info.emptyButtonLabel)
    }

    @Test
    fun `from without applied filters shows default empty label`() {
        val info = CarMaintenanceFilteringUiInfo.from(
            searchQuery = "",
            filterType = CarMaintenanceFilterType_All,
            filterMenuItems = listOf(),
            sortType = CarMaintenanceSortType.BY_DATE,
            sortMenuItems = listOf(),
        )

        assertEquals(R.string.no_car_maintenance, info.emptyLabel)
        assertEquals(R.string.no_car_maintenance_message, info.emptyMessage)
        assertEquals(R.string.add_car_maintenance, info.emptyButtonLabel)
    }
}

