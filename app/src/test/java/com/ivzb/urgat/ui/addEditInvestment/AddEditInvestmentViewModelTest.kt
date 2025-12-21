package com.ivzb.urgat.ui.addEditInvestment

import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.investment.FakeCreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.FakeDeleteInvestmentUseCase
import com.ivzb.urgat.domain.investment.FakeGetInvestmentUseCase
import com.ivzb.urgat.domain.investment.FakeUpdateInvestmentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditInvestmentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetInvestmentUseCase

    private lateinit var createUseCase: FakeCreateInvestmentUseCase

    private lateinit var updateUseCase: FakeUpdateInvestmentUseCase

    private lateinit var deleteUseCase: FakeDeleteInvestmentUseCase

    private lateinit var viewModel: AddEditInvestmentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetInvestmentUseCase()
        createUseCase = FakeCreateInvestmentUseCase()
        updateUseCase = FakeUpdateInvestmentUseCase()
        deleteUseCase = FakeDeleteInvestmentUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load investment`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads investment`() = runTest {
        val investmentId = "123"
        val investment = Investment(
            id = investmentId,
            name = "SNP 500",
            shares = BigDecimal("150"),
            cost = BigDecimal("250"),
            category = "etfs",
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = investment

        viewModel = createViewModel(id = investmentId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("SNP 500", state.name)
        assertEquals("150", state.shares)
        assertEquals("250", state.cost)
        assertEquals("etfs", state.category)
        assertEquals("USD", state.currency)
        assertEquals(1L, state.date)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateName updates uiState`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateName("SNP 500"))
        assertEquals("SNP 500", viewModel.uiState.value.name)
    }

    @Test
    fun `updateCurrency updates uiState`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency("USD"))
        assertEquals("USD", viewModel.uiState.value.currency)
    }

    @Test
    fun `updateShares updates uiState`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateShares("150"))
        assertEquals("150", viewModel.uiState.value.shares)
    }

    @Test
    fun `updateCategory updates uiState`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory("etfs"))
        assertEquals("etfs", viewModel.uiState.value.category)
    }

    @Test
    fun `saveInvestment with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)
        assertEquals(R.string.empty_investment_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveInvestment with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateName(" "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency(" "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateShares(" "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory(" "))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        assertEquals(R.string.empty_investment_message, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveInvestment creates new investment`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateName("SNP 500"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateShares("250"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCost("120"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory("etfs"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency("USD"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)
        
        advanceUntilIdle()

        val expected = listOf("SNP 500", BigDecimal("250"), BigDecimal("120"), "etfs", "USD", 1L)
        val actual = createUseCase.created
        assertEquals(expected, actual)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveInvestment creates new investment trimmed`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateName(" SNP 500 "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateShares(" 250 "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCost(" 370 "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory(" etfs "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency(" USD "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)

        advanceUntilIdle()

        val expected = listOf("SNP 500", BigDecimal("250"), BigDecimal("370"), "etfs", "USD", 1L)
        val actual = createUseCase.created
        assertEquals(expected, actual)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveInvestment updates existing investment`() = runTest {
        val investmentId = "123"
        val investment = Investment(
            id = investmentId,
            name = "SNP 500",
            shares = BigDecimal("150"),
            cost = BigDecimal("250"),
            category = "etfs",
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = investment

        viewModel = createViewModel(id = investmentId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(investment.name, uiState.name)
        assertEquals(investment.shares.toString(), uiState.shares)
        assertEquals(investment.cost.toString(), uiState.cost)
        assertEquals(investment.category, uiState.category)
        assertEquals(investment.currency, uiState.currency)
        assertEquals(investment.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditInvestmentIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateShares("370"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory("stocks"))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)

        advanceUntilIdle()

        val expected = updateUseCase.updated
        val actual = listOf(investmentId, "NVDA", BigDecimal("370"), BigDecimal("250"), "stocks", "EUR", 1L)
        assertEquals(expected, actual)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveInvestment updates existing investment trimmed`() = runTest {
        val investmentId = "123"
        val investment = Investment(
            id = investmentId,
            name = "SNP 500",
            shares = BigDecimal("150"),
            cost = BigDecimal("250"),
            category = "etfs",
            currency = "USD",
            date = 1L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = investment

        viewModel = createViewModel(id = investmentId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(investment.name, uiState.name)
        assertEquals(investment.shares.toString(), uiState.shares)
        assertEquals(investment.cost.toString(), uiState.cost)
        assertEquals(investment.category, uiState.category)
        assertEquals(investment.currency, uiState.currency)
        assertEquals(investment.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditInvestmentIntent.UpdateName(" NVDA "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency(" EUR "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateShares(" 370 "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCost(" 250 "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory(" stocks "))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateDate(1L))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)

        advanceUntilIdle()

        val expected = updateUseCase.updated
        val actual = listOf(investmentId, "NVDA", BigDecimal("370"), BigDecimal("250"), "stocks", "EUR", 1L)
        assertEquals(expected, actual)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteInvestment deletes existing investment`() = runTest {
        val investmentId = "123"

        viewModel = createViewModel(id = investmentId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditInvestmentIntent.DeleteInvestment)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(investmentId, id)
    }

    @Test
    fun `deleteInvestment deletes non-existing investment`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditInvestmentIntent.DeleteInvestment)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditInvestmentIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel(id: String?): AddEditInvestmentViewModel {
        val route = AddEditInvestmentRoute(id, if (id == null) R.string.add_investment else R.string.edit_investment)

        return AddEditInvestmentViewModel(
            route,
            getUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }
}