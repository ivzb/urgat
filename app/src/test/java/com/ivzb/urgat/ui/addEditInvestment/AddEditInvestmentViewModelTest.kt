package com.ivzb.urgat.ui.addEditInvestment

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.investment.FakeCreateInvestmentUseCase
import com.ivzb.urgat.domain.investment.FakeGetInvestmentUseCase
import com.ivzb.urgat.domain.investment.FakeUpdateInvestmentUseCase
import com.ivzb.urgat.navigation.AddEditInvestmentDestination
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

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditInvestmentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getInvestmentUseCase: FakeGetInvestmentUseCase

    private lateinit var createInvestmentUseCase: FakeCreateInvestmentUseCase

    private lateinit var updateInvestmentUseCase: FakeUpdateInvestmentUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: AddEditInvestmentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getInvestmentUseCase = FakeGetInvestmentUseCase()
        createInvestmentUseCase = FakeCreateInvestmentUseCase()
        updateInvestmentUseCase = FakeUpdateInvestmentUseCase()
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load investment`() = runTest {
        assertNull(getInvestmentUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads investment`() = runTest {
        val investmentId = "123"
        val investment = Investment(
            id = investmentId,
            name = "SNP 500",
            currency = "USD",
            amount = BigDecimal("150"),
            category = "etfs"
        )

        getInvestmentUseCase.toReturn = investment
        savedStateHandle[AddEditInvestmentDestination.Args.ARG_ID] = investmentId

        viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("SNP 500", state.name)
        assertEquals("USD", state.currency)
        assertEquals("150", state.amount)
        assertEquals("etfs", state.category)
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
    fun `updateAmount updates uiState`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateAmount("150"))
        assertEquals("150", viewModel.uiState.value.amount)
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
    fun `saveInvestment creates new investment`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.UpdateName("SNP 500"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency("USD"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateAmount("250"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory("etfs"))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)
        
        advanceUntilIdle()

        val (name, currency, amount, category) = createInvestmentUseCase.created
        assertEquals("SNP 500", name)
        assertEquals("USD", currency)
        assertEquals(BigDecimal("250"), amount)
        assertEquals("etfs", category)
        assertTrue(viewModel.uiState.value.isInvestmentSaved)
    }

    @Test
    fun `saveInvestment updates existing investment`() = runTest {
        val investmentId = "123"
        savedStateHandle[AddEditInvestmentDestination.Args.ARG_ID] = investmentId

        val investment = Investment(
            id = investmentId,
            name = "SNP 500",
            currency = "USD",
            amount = BigDecimal("150"),
            category = "etfs",
        )
        getInvestmentUseCase.toReturn = investment

        viewModel = createViewModel()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(investment.name, uiState.name)
        assertEquals(investment.currency, uiState.currency)
        assertEquals(investment.amount.toString(), uiState.amount)
        assertEquals(investment.category, uiState.category)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditInvestmentIntent.UpdateName("NVDA"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateAmount("370"))
        viewModel.onIntent(AddEditInvestmentIntent.UpdateCategory("stocks"))
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)

        advanceUntilIdle()

        val (id, name, currency, amount, category) = updateInvestmentUseCase.updated
        assertEquals(investmentId, id)
        assertEquals("NVDA", name)
        assertEquals("EUR", currency)
        assertEquals(BigDecimal("370"), amount)
        assertEquals("stocks", category)
        assertEquals(true, viewModel.uiState.value.isInvestmentSaved)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditInvestmentIntent.SaveInvestment)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditInvestmentIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = AddEditInvestmentViewModel(
        getInvestmentUseCase,
        createInvestmentUseCase,
        updateInvestmentUseCase,
        savedStateHandle
    )
}