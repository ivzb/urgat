package com.ivzb.urgat.ui.addEditCarMaintenance

import com.ivzb.urgat.R
import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.domain.carMaintenance.FakeCreateCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.FakeDeleteCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.FakeGetCarMaintenanceUseCase
import com.ivzb.urgat.domain.carMaintenance.FakeGetLastUsedCarMaintenanceCarNameUseCase
import com.ivzb.urgat.domain.carMaintenance.FakeUpdateCarMaintenanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditCarMaintenanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetCarMaintenanceUseCase

    private lateinit var getLastUsedCarNameUseCase: FakeGetLastUsedCarMaintenanceCarNameUseCase

    private lateinit var createUseCase: FakeCreateCarMaintenanceUseCase

    private lateinit var updateUseCase: FakeUpdateCarMaintenanceUseCase

    private lateinit var deleteUseCase: FakeDeleteCarMaintenanceUseCase

    private lateinit var viewModel: AddEditCarMaintenanceViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetCarMaintenanceUseCase()
        getLastUsedCarNameUseCase = FakeGetLastUsedCarMaintenanceCarNameUseCase()
        createUseCase = FakeCreateCarMaintenanceUseCase()
        updateUseCase = FakeUpdateCarMaintenanceUseCase()
        deleteUseCase = FakeDeleteCarMaintenanceUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load car maintenance`() = runTest {
        assertNull(getUseCase.calledWithId)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.withDelete)
        assertFalse(state.withDuplicate)
        assertTrue(state.withClearDefaults)
        assertEquals(R.string.add_car_maintenance, state.screenTitleResId)
    }

    @Test
    fun `init with id loads car maintenance`() = runTest {
        getUseCase.toReturn = carMaintenance

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(CAR_MAINTENANCE_ID, getUseCase.calledWithId)
        assertEquals("Oil change", state.description)
        assertEquals("ABC123", state.partNumber)
        assertEquals("https://example.com", state.url)
        assertEquals("Mercedes w140", state.carName)
        assertEquals("150000", state.odometer)
        assertEquals(123L, state.date)
        assertFalse(state.isLoading)
        assertTrue(state.withDelete)
        assertTrue(state.withDuplicate)
        assertFalse(state.withClearDefaults)
        assertFalse(state.shouldNavigateBack)
        assertEquals(R.string.edit_car_maintenance, state.screenTitleResId)
    }

    @Test
    fun `init with id stops loading when car maintenance is not found`() = runTest {
        getUseCase.toReturn = null

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.withDelete)
        assertEquals("", state.description)
    }

    @Test
    fun `init with id maps null partNumber and url to empty strings`() = runTest {
        getUseCase.toReturn = carMaintenance.copy(partNumber = null, url = null)

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.partNumber)
        assertEquals("", state.url)
    }

    @Test
    fun `updateDescription updates uiState`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription("Brake pads"))
        assertEquals("Brake pads", viewModel.uiState.value.description)
    }

    @Test
    fun `updatePartNumber updates uiState`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdatePartNumber("DEF456"))
        assertEquals("DEF456", viewModel.uiState.value.partNumber)
    }

    @Test
    fun `updateUrl updates uiState`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateUrl("https://parts.example.com"))
        assertEquals("https://parts.example.com", viewModel.uiState.value.url)
    }

    @Test
    fun `updateCarName updates uiState`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName("BMW e39"))
        assertEquals("BMW e39", viewModel.uiState.value.carName)
    }

    @Test
    fun `updateOdometer updates uiState`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer("175000"))
        assertEquals("175000", viewModel.uiState.value.odometer)
    }

    @Test
    fun `updateDate updates uiState`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDate(999L))
        assertEquals(999L, viewModel.uiState.value.date)
    }

    @Test
    fun `saveCarMaintenance with empty fields shows message and focuses first field`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.empty_car_maintenance_message, state.userMessage)
        assertEquals(0, state.focusIndex)
        assertTrue(createUseCase.created.isEmpty())
        assertFalse(state.shouldNavigateBack)
    }

    @Test
    fun `saveCarMaintenance with empty carName focuses carName field`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription("Oil change"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.empty_car_maintenance_message, state.userMessage)
        assertEquals(1, state.focusIndex)
        assertTrue(createUseCase.created.isEmpty())
    }

    @Test
    fun `saveCarMaintenance with empty odometer focuses odometer field`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription("Oil change"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName("Mercedes w140"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.empty_car_maintenance_message, state.userMessage)
        assertEquals(2, state.focusIndex)
        assertTrue(createUseCase.created.isEmpty())
    }

    @Test
    fun `saveCarMaintenance with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription(" "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdatePartNumber(" "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateUrl(" "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName(" "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer(" "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(createUseCase.created.isEmpty())
        assertEquals(R.string.empty_car_maintenance_message, state.userMessage)
        assertFalse(state.shouldNavigateBack)
    }

    @Test
    fun `saveCarMaintenance creates new car maintenance`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription("Oil change"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdatePartNumber("ABC123"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateUrl("https://example.com"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName("Mercedes w140"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer("150000"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDate(123L))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val (description, partNumber, url, carName, odometer, date) = createUseCase.created
        assertEquals("Oil change", description)
        assertEquals("ABC123", partNumber)
        assertEquals("https://example.com", url)
        assertEquals("Mercedes w140", carName)
        assertEquals(150000, odometer)
        assertEquals(123L, date)
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveCarMaintenance creates new car maintenance trimmed`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription(" Oil change "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdatePartNumber(" ABC123 "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateUrl(" https://example.com "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName(" Mercedes w140 "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer(" 150000 "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val (description, partNumber, url, carName, odometer) = createUseCase.created
        assertEquals("Oil change", description)
        assertEquals("ABC123", partNumber)
        assertEquals("https://example.com", url)
        assertEquals("Mercedes w140", carName)
        assertEquals(150000, odometer)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveCarMaintenance creates new car maintenance with null url when url is blank`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription("Oil change"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName("Mercedes w140"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer("150000"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateUrl("   "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val (_, _, url) = createUseCase.created
        assertNull(url)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveCarMaintenance updates existing car maintenance`() = runTest {
        getUseCase.toReturn = carMaintenance

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription("Brake pads"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer("160000"))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val (id, description, partNumber, url, carName, odometer) = updateUseCase.updated
        assertEquals(CAR_MAINTENANCE_ID, id)
        assertEquals("Brake pads", description)
        assertEquals("ABC123", partNumber)
        assertEquals("https://example.com", url)
        assertEquals("Mercedes w140", carName)
        assertEquals(160000, odometer)
        assertTrue(createUseCase.created.isEmpty())
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveCarMaintenance updates existing car maintenance trimmed`() = runTest {
        getUseCase.toReturn = carMaintenance

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateDescription(" Brake pads "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateCarName(" BMW e39 "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.UpdateOdometer(" 160000 "))
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)

        advanceUntilIdle()

        val (id, description, _, _, carName, odometer) = updateUseCase.updated
        assertEquals(CAR_MAINTENANCE_ID, id)
        assertEquals("Brake pads", description)
        assertEquals("BMW e39", carName)
        assertEquals(160000, odometer)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteCarMaintenance deletes existing car maintenance`() = runTest {
        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        viewModel.onIntent(AddEditCarMaintenanceIntent.DeleteCarMaintenance)

        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(CAR_MAINTENANCE_ID, id)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteCarMaintenance without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditCarMaintenanceIntent.DeleteCarMaintenance)

            val state = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, state.userMessage)
        }

        assertTrue(deleteUseCase.deleted.isEmpty())
    }

    @Test
    fun `duplicateCarMaintenance creates a new car maintenance with a fresh date`() = runTest {
        getUseCase.toReturn = carMaintenance

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        viewModel.onIntent(AddEditCarMaintenanceIntent.DuplicateCarMaintenance)

        advanceUntilIdle()

        val (description, partNumber, url, carName, odometer, date) = createUseCase.created
        assertEquals("Oil change", description)
        assertEquals("ABC123", partNumber)
        assertEquals("https://example.com", url)
        assertEquals("Mercedes w140", carName)
        assertEquals(150000, odometer)
        assertTrue((date as Long) > 123L)
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateCarMaintenance without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditCarMaintenanceIntent.DuplicateCarMaintenance)

            val state = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, state.userMessage)
        }

        assertTrue(createUseCase.created.isEmpty())
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditCarMaintenanceIntent.SaveCarMaintenance)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditCarMaintenanceIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun `init without id autofills last used car name`() = runTest {
        getLastUsedCarNameUseCase.toReturn = "Mercedes w140"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("Mercedes w140", viewModel.uiState.value.carName)
    }

    @Test
    fun `init without id and no last used car name keeps empty value`() = runTest {
        getLastUsedCarNameUseCase.toReturn = null

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.carName)
    }

    @Test
    fun `init with id does not autofill last used car name`() = runTest {
        getLastUsedCarNameUseCase.toReturn = "BMW e39"
        getUseCase.toReturn = carMaintenance

        viewModel = createViewModel(id = CAR_MAINTENANCE_ID)

        advanceUntilIdle()

        assertEquals("Mercedes w140", viewModel.uiState.value.carName)
    }

    @Test
    fun `clearDefaults clears carName`() = runTest {
        getLastUsedCarNameUseCase.toReturn = "Mercedes w140"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("Mercedes w140", viewModel.uiState.value.carName)

        viewModel.onIntent(AddEditCarMaintenanceIntent.ClearDefaults)

        assertEquals("", viewModel.uiState.value.carName)
    }

    private fun createViewModel(id: String?): AddEditCarMaintenanceViewModel {
        val route = AddEditCarMaintenanceRoute(
            id,
            if (id == null) R.string.add_car_maintenance else R.string.edit_car_maintenance,
        )

        return AddEditCarMaintenanceViewModel(
            route,
            getUseCase,
            getLastUsedCarNameUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }

    private operator fun <T> List<T>.component6(): T = this[5]

    companion object {

        private const val CAR_MAINTENANCE_ID = "123"

        private val carMaintenance = CarMaintenance(
            id = CAR_MAINTENANCE_ID,
            description = "Oil change",
            partNumber = "ABC123",
            url = "https://example.com",
            carName = "Mercedes w140",
            odometer = 150000,
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )

    }

}

