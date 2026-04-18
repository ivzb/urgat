package com.ivzb.urgat.domain.carMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.carMaintenance.CarMaintenanceSortType
import com.ivzb.urgat.ui.components.SortMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveCarMaintenanceSortingUseCaseTest {

    @Test
    fun `invoke returns all sort options`() = runTest {
        val useCase = DefaultObserveCarMaintenanceSortingUseCase()

        val result = useCase().first()

        val items = (result as Async.Success).data
        assertEquals(3, items.size)
        assertEquals(CarMaintenanceSortType.BY_DATE, items[0].type)
        assertEquals(CarMaintenanceSortType.BY_ODOMETER_MIN, items[1].type)
        assertEquals(CarMaintenanceSortType.BY_ODOMETER_MAX, items[2].type)
    }

    @Test
    fun `invoke returns correct labels`() = runTest {
        val useCase = DefaultObserveCarMaintenanceSortingUseCase()

        val result = useCase().first()

        val items = (result as Async.Success).data
        assertEquals(R.string.sort_by_date, items[0].labelResId)
        assertEquals(R.string.sort_by_odometer_min, items[1].labelResId)
        assertEquals(R.string.sort_by_odometer_max, items[2].labelResId)
    }
}

