package com.ivzb.urgat.ui.addEditFuelConsumption

import com.ivzb.urgat.R
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.domain.fuelConsumption.FakeCreateFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.FakeDeleteFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.FakeGetFuelConsumptionUseCase
import com.ivzb.urgat.domain.fuelConsumption.FakeGetLastUsedFuelConsumptionCarNameUseCase
import com.ivzb.urgat.domain.fuelConsumption.FakeGetLastUsedFuelConsumptionCurrencyUseCase
import com.ivzb.urgat.domain.fuelConsumption.FakeGetLastUsedFuelConsumptionTypeUseCase
import com.ivzb.urgat.domain.fuelConsumption.FakeUpdateFuelConsumptionUseCase
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
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditFuelConsumptionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetFuelConsumptionUseCase

    private lateinit var getLastUsedCurrencyUseCase: FakeGetLastUsedFuelConsumptionCurrencyUseCase

    private lateinit var getLastUsedTypeUseCase: FakeGetLastUsedFuelConsumptionTypeUseCase

    private lateinit var getLastUsedCarNameUseCase: FakeGetLastUsedFuelConsumptionCarNameUseCase

    private lateinit var createUseCase: FakeCreateFuelConsumptionUseCase

    private lateinit var updateUseCase: FakeUpdateFuelConsumptionUseCase

    private lateinit var deleteUseCase: FakeDeleteFuelConsumptionUseCase

    private lateinit var viewModel: AddEditFuelConsumptionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetFuelConsumptionUseCase()
        getLastUsedCurrencyUseCase = FakeGetLastUsedFuelConsumptionCurrencyUseCase()
        getLastUsedTypeUseCase = FakeGetLastUsedFuelConsumptionTypeUseCase()
        getLastUsedCarNameUseCase = FakeGetLastUsedFuelConsumptionCarNameUseCase()
        createUseCase = FakeCreateFuelConsumptionUseCase()
        updateUseCase = FakeUpdateFuelConsumptionUseCase()
        deleteUseCase = FakeDeleteFuelConsumptionUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load fuel consumption`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads fuel consumption`() = runTest {
        val fuelConsumptionId = "123"
        val fuelConsumption = FuelConsumption(
            id = fuelConsumptionId,
            amount = BigDecimal("40"),
            price = BigDecimal("85"),
            currency = "EUR",
            type = "Diesel",
            carName = "Toyota",
            odometer = 50000,
            distance = 500,
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = fuelConsumption

        viewModel = createViewModel(id = fuelConsumptionId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("40", state.amount)
        assertEquals("85", state.price)
        assertEquals("EUR", state.currency)
        assertEquals("Diesel", state.type)
        assertEquals("Toyota", state.carName)
        assertEquals("50000", state.odometer)
        assertEquals(123L, state.date)
        assertEquals(false, state.isLoading)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateAmount updates uiState`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount("30"))
        assertEquals("30", viewModel.uiState.value.amount)
    }

    @Test
    fun `updatePrice updates uiState`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice("185"))
        assertEquals("185", viewModel.uiState.value.price)
    }

    @Test
    fun `updateCurrency updates uiState`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCurrency("BGN"))
        assertEquals("BGN", viewModel.uiState.value.currency)
    }

    @Test
    fun `updateType updates uiState`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateType("Gasoline"))
        assertEquals("Gasoline", viewModel.uiState.value.type)
    }

    @Test
    fun `updateCarName updates uiState`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCarName("Honda"))
        assertEquals("Honda", viewModel.uiState.value.carName)
    }

    @Test
    fun `updateOdometer updates uiState`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateOdometer("75000"))
        assertEquals("75000", viewModel.uiState.value.odometer)
    }

    @Test
    fun `saveFuelConsumption with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)
        assertEquals(R.string.empty_fuel_consumption_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveFuelConsumption with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount(" "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice(" "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCurrency(" "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateType(" "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCarName(" "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateOdometer(" "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        assertEquals(R.string.empty_fuel_consumption_message, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveFuelConsumption creates new fuel consumption`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount("40"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice("85"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateType("Diesel"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCarName("Toyota"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateOdometer("50000"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)

        advanceUntilIdle()

        val (amount, price, currency, type, carName, odometer) = createUseCase.created
        assertEquals(BigDecimal("40"), amount)
        assertEquals(BigDecimal("85"), price)
        assertEquals("EUR", currency)
        assertEquals("Diesel", type)
        assertEquals("Toyota", carName)
        assertEquals(50000, odometer)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveFuelConsumption creates new fuel consumption trimmed`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount(" 40 "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice(" 85 "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCurrency(" EUR "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateType(" Diesel "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateCarName(" Toyota "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateOdometer(" 50000 "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)

        advanceUntilIdle()

        val (amount, price, currency, type, carName, odometer) = createUseCase.created
        assertEquals(BigDecimal("40"), amount)
        assertEquals(BigDecimal("85"), price)
        assertEquals("EUR", currency)
        assertEquals("Diesel", type)
        assertEquals("Toyota", carName)
        assertEquals(50000, odometer)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveFuelConsumption updates existing fuel consumption`() = runTest {
        val fuelConsumptionId = "123"
        val fuelConsumption = FuelConsumption(
            id = fuelConsumptionId,
            amount = BigDecimal("40"),
            price = BigDecimal("85"),
            currency = "EUR",
            type = "Diesel",
            carName = "Toyota",
            odometer = 50000,
            distance = 500,
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = fuelConsumption

        viewModel = createViewModel(fuelConsumptionId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(fuelConsumption.amount.toString(), uiState.amount)
        assertEquals(fuelConsumption.price.toString(), uiState.price)
        assertEquals(fuelConsumption.currency, uiState.currency)
        assertEquals(fuelConsumption.type, uiState.type)
        assertEquals(fuelConsumption.carName, uiState.carName)
        assertEquals(fuelConsumption.odometer.toString(), uiState.odometer)
        assertEquals(fuelConsumption.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount("50"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice("60"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateType("Gasoline"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateOdometer("60000"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)

        advanceUntilIdle()

        val (id, amount, price, currency, type, carName, odometer) = updateUseCase.updated
        assertEquals(fuelConsumptionId, id)
        assertEquals(BigDecimal("50"), amount)
        assertEquals(BigDecimal("60"), price)
        assertEquals("EUR", currency)
        assertEquals("Gasoline", type)
        assertEquals("Toyota", carName)
        assertEquals(60000, odometer)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveFuelConsumption updates existing fuel consumption trimmed`() = runTest {
        val fuelConsumptionId = "123"
        val fuelConsumption = FuelConsumption(
            id = fuelConsumptionId,
            amount = BigDecimal("40"),
            price = BigDecimal("75"),
            currency = "EUR",
            type = "Diesel",
            carName = "Toyota",
            odometer = 50000,
            distance = 500,
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = fuelConsumption

        viewModel = createViewModel(fuelConsumptionId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount(" 50 "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice(" 80 "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateType(" Gasoline "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateOdometer(" 60000 "))
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)

        advanceUntilIdle()

        val (id, amount, price, currency, type, carName, odometer) = updateUseCase.updated
        assertEquals(fuelConsumptionId, id)
        assertEquals(BigDecimal("50"), amount)
        assertEquals(BigDecimal("80"), price)
        assertEquals("EUR", currency)
        assertEquals("Gasoline", type)
        assertEquals("Toyota", carName)
        assertEquals(60000, odometer)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteFuelConsumption deletes existing fuel consumption`() = runTest {
        val fuelConsumptionId = "123"

        viewModel = createViewModel(fuelConsumptionId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditFuelConsumptionIntent.DeleteFuelConsumption)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(fuelConsumptionId, id)
    }

    @Test
    fun `deleteFuelConsumption deletes non-existing fuel consumption`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditFuelConsumptionIntent.DeleteFuelConsumption)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `duplicateFuelConsumption creates new fuel consumption with current date`() = runTest {
        val fuelConsumptionId = "123"
        val fuelConsumption = FuelConsumption(
            id = fuelConsumptionId,
            amount = BigDecimal("40"),
            price = BigDecimal("75"),
            currency = "EUR",
            type = "Diesel",
            carName = "Toyota",
            odometer = 50000,
            distance = 500,
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = fuelConsumption

        viewModel = createViewModel(fuelConsumptionId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditFuelConsumptionIntent.DuplicateFuelConsumption)

        advanceUntilIdle()

        val (amount, price, currency, type, carName, odometer, date) = createUseCase.created
        assertEquals(BigDecimal("40"), amount)
        assertEquals(BigDecimal("75"), price)
        assertEquals("EUR", currency)
        assertEquals("Diesel", type)
        assertEquals("Toyota", carName)
        assertEquals(50000, odometer)
        assertTrue((date as Long) > 123L)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateFuelConsumption with modified fields creates new fuel consumption`() = runTest {
        val fuelConsumptionId = "456"
        val fuelConsumption = FuelConsumption(
            id = fuelConsumptionId,
            amount = BigDecimal("40"),
            price = BigDecimal("75"),
            currency = "EUR",
            type = "Diesel",
            carName = "Toyota",
            odometer = 50000,
            distance = 500,
            date = 100L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = fuelConsumption

        viewModel = createViewModel(fuelConsumptionId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdateAmount("60"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.UpdatePrice("80"))
        viewModel.onIntent(AddEditFuelConsumptionIntent.DuplicateFuelConsumption)

        advanceUntilIdle()

        val (amount, price, currency, type, carName, odometer) = createUseCase.created
        assertEquals(BigDecimal("60"), amount)
        assertEquals(BigDecimal("80"), price)
        assertEquals("EUR", currency)
        assertEquals("Diesel", type)
        assertEquals("Toyota", carName)
        assertEquals(50000, odometer)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateFuelConsumption without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditFuelConsumptionIntent.DuplicateFuelConsumption)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditFuelConsumptionIntent.SaveFuelConsumption)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditFuelConsumptionIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun `init without id autofills last used defaults`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "USD"
        getLastUsedTypeUseCase.toReturn = "Diesel"
        getLastUsedCarNameUseCase.toReturn = "Toyota"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("USD", viewModel.uiState.value.currency)
        assertEquals("Diesel", viewModel.uiState.value.type)
        assertEquals("Toyota", viewModel.uiState.value.carName)
    }

    @Test
    fun `init without id and no last used defaults keeps empty values`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = null
        getLastUsedTypeUseCase.toReturn = null
        getLastUsedCarNameUseCase.toReturn = null

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.currency)
        assertEquals("", viewModel.uiState.value.type)
        assertEquals("", viewModel.uiState.value.carName)
    }

    @Test
    fun `init with id does not autofill last used defaults`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "USD"
        getLastUsedTypeUseCase.toReturn = "Gasoline"
        getLastUsedCarNameUseCase.toReturn = "Honda"
        val fuelConsumptionId = "123"
        val fuelConsumption = FuelConsumption(
            id = fuelConsumptionId,
            amount = BigDecimal("40"),
            price = BigDecimal("85"),
            currency = "EUR",
            type = "Diesel",
            carName = "Toyota",
            odometer = 50000,
            distance = 500,
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = fuelConsumption

        viewModel = createViewModel(id = fuelConsumptionId)

        advanceUntilIdle()

        assertEquals("EUR", viewModel.uiState.value.currency)
        assertEquals("Diesel", viewModel.uiState.value.type)
        assertEquals("Toyota", viewModel.uiState.value.carName)
    }

    @Test
    fun `clearDefaults clears currency type and carName`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "USD"
        getLastUsedTypeUseCase.toReturn = "Diesel"
        getLastUsedCarNameUseCase.toReturn = "Toyota"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("USD", viewModel.uiState.value.currency)
        assertEquals("Diesel", viewModel.uiState.value.type)
        assertEquals("Toyota", viewModel.uiState.value.carName)

        viewModel.onIntent(AddEditFuelConsumptionIntent.ClearDefaults)

        assertEquals("", viewModel.uiState.value.currency)
        assertEquals("", viewModel.uiState.value.type)
        assertEquals("", viewModel.uiState.value.carName)
    }

    private fun createViewModel(id: String?): AddEditFuelConsumptionViewModel {
        val route = AddEditFuelConsumptionRoute(
            id,
            if (id == null) R.string.add_fuel_consumption else R.string.edit_fuel_consumption,
        )

        return AddEditFuelConsumptionViewModel(
            route,
            getUseCase,
            getLastUsedCurrencyUseCase,
            getLastUsedTypeUseCase,
            getLastUsedCarNameUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }

    private operator fun <T> List<T>.component6(): T = this[5]

    private operator fun <T> List<T>.component7(): T = this[6]

}
