package com.ivzb.urgat.ui.addEditTrade

import com.ivzb.urgat.R
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.domain.trade.FakeCreateTradeUseCase
import com.ivzb.urgat.domain.trade.FakeDeleteTradeUseCase
import com.ivzb.urgat.domain.trade.FakeGetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.FakeGetLastUsedTradeNameUseCase
import com.ivzb.urgat.domain.trade.FakeGetTradeUseCase
import com.ivzb.urgat.domain.trade.FakeUpdateTradeUseCase
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
class AddEditTradeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetTradeUseCase
    private lateinit var getLastUsedNameUseCase: FakeGetLastUsedTradeNameUseCase
    private lateinit var getLastUsedCurrencyUseCase: FakeGetLastUsedTradeCurrencyUseCase
    private lateinit var createUseCase: FakeCreateTradeUseCase
    private lateinit var updateUseCase: FakeUpdateTradeUseCase
    private lateinit var deleteUseCase: FakeDeleteTradeUseCase

    private lateinit var viewModel: AddEditTradeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetTradeUseCase()
        getLastUsedNameUseCase = FakeGetLastUsedTradeNameUseCase()
        getLastUsedCurrencyUseCase = FakeGetLastUsedTradeCurrencyUseCase()
        createUseCase = FakeCreateTradeUseCase()
        updateUseCase = FakeUpdateTradeUseCase()
        deleteUseCase = FakeDeleteTradeUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load trade`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads trade`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("AAPL", state.name)
        assertEquals("150", state.amount)
        assertEquals("USD", state.currency)
        assertEquals(1L, state.date)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateName updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateName("AAPL"))
        assertEquals("AAPL", viewModel.uiState.value.name)
    }

    @Test
    fun `updateAmount updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateAmount("150"))
        assertEquals("150", viewModel.uiState.value.amount)
    }

    @Test
    fun `updateCurrency updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("USD"))
        assertEquals("USD", viewModel.uiState.value.currency)
    }

    @Test
    fun `saveTrade with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)
        assertEquals(R.string.empty_trade_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveTrade with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateName(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateAmount(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency(" "))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        assertEquals(R.string.empty_trade_message, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveTrade creates new trade`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateName("AAPL"))
        viewModel.onIntent(AddEditTradeIntent.UpdateAmount("150"))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("USD"))
        viewModel.onIntent(AddEditTradeIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf("AAPL", BigDecimal("150"), "USD", 1L)
        assertEquals(expected, createUseCase.created)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTrade creates new trade trimmed`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateName(" AAPL "))
        viewModel.onIntent(AddEditTradeIntent.UpdateAmount(" 150 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency(" USD "))
        viewModel.onIntent(AddEditTradeIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf("AAPL", BigDecimal("150"), "USD", 1L)
        assertEquals(expected, createUseCase.created)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTrade updates existing trade`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf(tradeId, "NVDA", BigDecimal("150"), "USD", 1L)
        assertEquals(expected, updateUseCase.updated)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTrade updates existing trade trimmed`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.UpdateName(" NVDA "))
        viewModel.onIntent(AddEditTradeIntent.UpdateAmount(" 200 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency(" EUR "))
        viewModel.onIntent(AddEditTradeIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf(tradeId, "NVDA", BigDecimal("200"), "EUR", 1L)
        assertEquals(expected, updateUseCase.updated)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteTrade deletes existing trade`() = runTest {
        val tradeId = "123"

        viewModel = createViewModel(id = tradeId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.DeleteTrade)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(tradeId, id)
    }

    @Test
    fun `deleteTrade deletes non-existing trade`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditTradeIntent.DeleteTrade)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `duplicateTrade resets state to create mode with current data`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.DuplicateTrade)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.add_trade, state.screenTitleResId)
        assertEquals("AAPL", state.name)
        assertEquals("150", state.amount)
        assertEquals("USD", state.currency)
        assertTrue(state.date > 123L)
        assertFalse(state.withDelete)
        assertFalse(state.withDuplicate)
        assertFalse(state.shouldNavigateBack)
    }

    @Test
    fun `duplicateTrade then save creates new trade`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.DuplicateTrade)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isNotEmpty())
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateTrade with modified fields then save creates new trade`() = runTest {
        val tradeId = "456"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditTradeIntent.UpdateAmount("200"))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditTradeIntent.DuplicateTrade)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.add_trade, state.screenTitleResId)
        assertTrue(createUseCase.created.isNotEmpty())
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(state.shouldNavigateBack)
    }

    @Test
    fun `duplicateTrade without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditTradeIntent.DuplicateTrade)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditTradeIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun `init without id autofills last used name`() = runTest {
        getLastUsedNameUseCase.toReturn = "AAPL"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("AAPL", viewModel.uiState.value.name)
    }

    @Test
    fun `init without id and no last name keeps empty name`() = runTest {
        getLastUsedNameUseCase.toReturn = null

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.name)
    }

    @Test
    fun `init without id autofills last used currency`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "USD"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("USD", viewModel.uiState.value.currency)
    }

    @Test
    fun `init without id and no last currency keeps empty currency`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = null

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.currency)
    }

    @Test
    fun `init with id does not autofill last used values`() = runTest {
        getLastUsedNameUseCase.toReturn = "NVDA"
        getLastUsedCurrencyUseCase.toReturn = "GBP"
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            amount = BigDecimal("150"),
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("AAPL", state.name)
        assertEquals("USD", state.currency)
    }

    private fun createViewModel(id: String?): AddEditTradeViewModel {
        val route = AddEditTradeRoute(id, if (id == null) R.string.add_trade else R.string.edit_trade)

        return AddEditTradeViewModel(
            route,
            getUseCase,
            getLastUsedNameUseCase,
            getLastUsedCurrencyUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }
}

