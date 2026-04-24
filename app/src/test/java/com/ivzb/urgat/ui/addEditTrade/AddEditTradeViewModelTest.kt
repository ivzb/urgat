package com.ivzb.urgat.ui.addEditTrade

import com.ivzb.urgat.R
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.domain.trade.FakeCreateTradeUseCase
import com.ivzb.urgat.domain.trade.FakeDeleteTradeUseCase
import com.ivzb.urgat.domain.trade.FakeGetLastUsedTradeCurrencyUseCase
import com.ivzb.urgat.domain.trade.FakeGetLastUsedTradeFeeUseCase
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
    private lateinit var getLastUsedCurrencyUseCase: FakeGetLastUsedTradeCurrencyUseCase
    private lateinit var getLastUsedNameUseCase: FakeGetLastUsedTradeNameUseCase
    private lateinit var getLastUsedFeeUseCase: FakeGetLastUsedTradeFeeUseCase
    private lateinit var createUseCase: FakeCreateTradeUseCase
    private lateinit var updateUseCase: FakeUpdateTradeUseCase
    private lateinit var deleteUseCase: FakeDeleteTradeUseCase

    private lateinit var viewModel: AddEditTradeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetTradeUseCase()
        getLastUsedCurrencyUseCase = FakeGetLastUsedTradeCurrencyUseCase()
        getLastUsedNameUseCase = FakeGetLastUsedTradeNameUseCase()
        getLastUsedFeeUseCase = FakeGetLastUsedTradeFeeUseCase()
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
            shares = BigDecimal("10"),
            marketPrice = BigDecimal("150"),
            fee = BigDecimal("1.50"),
            currency = "USD",
            orderType = "Buy",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("AAPL", state.name)
        assertEquals("10", state.shares)
        assertEquals("150", state.marketPrice)
        assertEquals("1.50", state.fee)
        assertEquals("USD", state.currency)
        assertEquals("Buy", state.orderType)
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
    fun `updateShares updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateShares("10"))
        assertEquals("10", viewModel.uiState.value.shares)
    }

    @Test
    fun `updateFee updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateFee("1.50"))
        assertEquals("1.50", viewModel.uiState.value.fee)
    }

    @Test
    fun `updateMarketPrice updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateMarketPrice("150"))
        assertEquals("150", viewModel.uiState.value.marketPrice)
    }

    @Test
    fun `updateCurrency updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("USD"))
        assertEquals("USD", viewModel.uiState.value.currency)
    }

    @Test
    fun `updateOrderType updates uiState`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateOrderType("Buy"))
        assertEquals("Buy", viewModel.uiState.value.orderType)
    }

    @Test
    fun `saveTrade with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)
        assertEquals(R.string.empty_trade_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveTrade with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateName(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateShares(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateFee(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateMarketPrice(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency(" "))
        viewModel.onIntent(AddEditTradeIntent.UpdateOrderType(" "))
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
        viewModel.onIntent(AddEditTradeIntent.UpdateShares("10"))
        viewModel.onIntent(AddEditTradeIntent.UpdateFee("1.50"))
        viewModel.onIntent(AddEditTradeIntent.UpdateMarketPrice("150"))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("USD"))
        viewModel.onIntent(AddEditTradeIntent.UpdateOrderType("Buy"))
        viewModel.onIntent(AddEditTradeIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf("AAPL", BigDecimal("10"), BigDecimal("1.50"), BigDecimal("150"), "USD", "Buy", 1L)
        assertEquals(expected, createUseCase.created)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTrade creates new trade trimmed`() = runTest {
        viewModel.onIntent(AddEditTradeIntent.UpdateName(" AAPL "))
        viewModel.onIntent(AddEditTradeIntent.UpdateShares(" 10 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateFee(" 1.50 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateMarketPrice(" 150 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency(" USD "))
        viewModel.onIntent(AddEditTradeIntent.UpdateOrderType(" Buy "))
        viewModel.onIntent(AddEditTradeIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf("AAPL", BigDecimal("10"), BigDecimal("1.50"), BigDecimal("150"), "USD", "Buy", 1L)
        assertEquals(expected, createUseCase.created)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTrade updates existing trade`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            shares = BigDecimal("10"),
            marketPrice = BigDecimal("150"),
            fee = BigDecimal("1.50"),
            currency = "USD",
            orderType = "Buy",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf(tradeId, "NVDA", BigDecimal("10"), BigDecimal("1.50"), BigDecimal("150"), "EUR", "Buy", 1L)
        assertEquals(expected, updateUseCase.updated)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveTrade updates existing trade trimmed`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            shares = BigDecimal("10"),
            marketPrice = BigDecimal("150"),
            fee = BigDecimal("1.50"),
            currency = "USD",
            orderType = "Buy",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.UpdateName(" NVDA "))
        viewModel.onIntent(AddEditTradeIntent.UpdateShares(" 20 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateFee(" 2.00 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateMarketPrice(" 200 "))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency(" EUR "))
        viewModel.onIntent(AddEditTradeIntent.UpdateOrderType(" Sell "))
        viewModel.onIntent(AddEditTradeIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditTradeIntent.SaveTrade)

        advanceUntilIdle()

        val expected = listOf(tradeId, "NVDA", BigDecimal("20"), BigDecimal("2.00"), BigDecimal("200"), "EUR", "Sell", 1L)
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
    fun `duplicateTrade creates new trade with current date`() = runTest {
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            shares = BigDecimal("10"),
            marketPrice = BigDecimal("150"),
            fee = BigDecimal("1.50"),
            currency = "USD",
            orderType = "Buy",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.DuplicateTrade)

        advanceUntilIdle()

        val (name, shares, fee, marketPrice, currency, orderType, date) = createUseCase.created
        assertEquals("AAPL", name)
        assertEquals(BigDecimal("10"), shares)
        assertEquals(BigDecimal("1.50"), fee)
        assertEquals(BigDecimal("150"), marketPrice)
        assertEquals("USD", currency)
        assertEquals("Buy", orderType)
        assertTrue((date as Long) > 123L)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateTrade with modified fields creates new trade`() = runTest {
        val tradeId = "456"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            shares = BigDecimal("10"),
            marketPrice = BigDecimal("150"),
            fee = BigDecimal("1.50"),
            currency = "USD",
            orderType = "Buy",
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(tradeId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditTradeIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditTradeIntent.UpdateShares("20"))
        viewModel.onIntent(AddEditTradeIntent.UpdateFee("2.00"))
        viewModel.onIntent(AddEditTradeIntent.UpdateMarketPrice("200"))
        viewModel.onIntent(AddEditTradeIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditTradeIntent.UpdateOrderType("Sell"))
        viewModel.onIntent(AddEditTradeIntent.DuplicateTrade)

        advanceUntilIdle()

        val (name, shares, fee, marketPrice, currency, orderType, date) = createUseCase.created
        assertEquals("NVDA", name)
        assertEquals(BigDecimal("20"), shares)
        assertEquals(BigDecimal("2.00"), fee)
        assertEquals(BigDecimal("200"), marketPrice)
        assertEquals("EUR", currency)
        assertEquals("Sell", orderType)
        assertTrue((date as Long) > 123L)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
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
    fun `init without id autofills last used fee`() = runTest {
        getLastUsedFeeUseCase.toReturn = "1.50"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("1.50", viewModel.uiState.value.fee)
    }

    @Test
    fun `init without id and no last fee keeps empty fee`() = runTest {
        getLastUsedFeeUseCase.toReturn = null

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.fee)
    }

    @Test
    fun `init without id autofills all last used values`() = runTest {
        getLastUsedNameUseCase.toReturn = "AAPL"
        getLastUsedFeeUseCase.toReturn = "1.50"
        getLastUsedCurrencyUseCase.toReturn = "USD"

        viewModel = createViewModel(id = null)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("AAPL", state.name)
        assertEquals("1.50", state.fee)
        assertEquals("USD", state.currency)
    }

    @Test
    fun `init with id does not autofill last used values`() = runTest {
        getLastUsedNameUseCase.toReturn = "NVDA"
        getLastUsedFeeUseCase.toReturn = "2.00"
        getLastUsedCurrencyUseCase.toReturn = "GBP"
        val tradeId = "123"
        val trade = Trade(
            id = tradeId,
            name = "AAPL",
            shares = BigDecimal("10"),
            marketPrice = BigDecimal("150"),
            fee = BigDecimal("1.50"),
            currency = "USD",
            orderType = "Buy",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = trade

        viewModel = createViewModel(id = tradeId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("AAPL", state.name)
        assertEquals("1.50", state.fee)
        assertEquals("USD", state.currency)
    }

    private fun createViewModel(id: String?): AddEditTradeViewModel {
        val route = AddEditTradeRoute(id, if (id == null) R.string.add_trade else R.string.edit_trade)

        return AddEditTradeViewModel(
            route,
            getUseCase,
            getLastUsedCurrencyUseCase,
            getLastUsedNameUseCase,
            getLastUsedFeeUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }

    private operator fun <T> List<T>.component6(): T = this[5]
    private operator fun <T> List<T>.component7(): T = this[6]
    private operator fun <T> List<T>.component8(): T = this[7]
}

