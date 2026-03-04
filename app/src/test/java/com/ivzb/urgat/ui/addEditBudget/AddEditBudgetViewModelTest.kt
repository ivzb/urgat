package com.ivzb.urgat.ui.addEditBudget

import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.domain.budget.FakeCreateBudgetUseCase
import com.ivzb.urgat.domain.budget.FakeDeleteBudgetUseCase
import com.ivzb.urgat.domain.budget.FakeGetBudgetUseCase
import com.ivzb.urgat.domain.budget.FakeUpdateBudgetUseCase
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
class AddEditBudgetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetBudgetUseCase

    private lateinit var createUseCase: FakeCreateBudgetUseCase

    private lateinit var updateUseCase: FakeUpdateBudgetUseCase

    private lateinit var deleteUseCase: FakeDeleteBudgetUseCase

    private lateinit var viewModel: AddEditBudgetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetBudgetUseCase()
        createUseCase = FakeCreateBudgetUseCase()
        updateUseCase = FakeUpdateBudgetUseCase()
        deleteUseCase = FakeDeleteBudgetUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load Budget`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads budget`() = runTest {
        val budgetId = "123"
        val budget = Budget(
            id = budgetId,
            income = 6500.toBigDecimal(),
            bankStart = 11000.toBigDecimal(),
            bankEnd = 8400.toBigDecimal(),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = budget

        viewModel = createViewModel(id = budgetId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("6500", state.income)
        assertEquals("11000", state.bankStart)
        assertEquals("8400", state.bankEnd)
        assertEquals(123L, state.date)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateIncome updates uiState`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome("6500"))
        assertEquals("6500", viewModel.uiState.value.income)
    }

    @Test
    fun `updateBankStart updates uiState`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart("11000"))
        assertEquals("11000", viewModel.uiState.value.bankStart)
    }

    @Test
    fun `updateBankEnd updates uiState`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd("8400"))
        assertEquals("8400", viewModel.uiState.value.bankEnd)
    }

    @Test
    fun `updateDate updates uiState`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateDate(123L))
        assertEquals(123L, viewModel.uiState.value.date)
    }

    @Test
    fun `saveBudget with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)
        assertEquals(R.string.empty_budget_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveBudget with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome(" "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart(" "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd(" "))
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        assertEquals(R.string.empty_budget_message, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveBudget creates new budget`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome("6500"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart("11000"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd("8400"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateDate(123L))
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)
        
        advanceUntilIdle()

        val expected = listOf(BigDecimal("6500"), BigDecimal("11000"), BigDecimal("8400"), 123L)
        val actual = createUseCase.created
        assertEquals(expected, actual)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveBudget creates new budget trimmed`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome(" 6500 "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart(" 11000 "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd(" 8400 "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateDate(123L))
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)

        advanceUntilIdle()

        val expected = listOf(BigDecimal("6500"), BigDecimal("11000"), BigDecimal("8400"), 123L)
        val actual = createUseCase.created
        assertEquals(expected, actual)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveBudget updates existing budget`() = runTest {
        val budgetId = "123"
        val budget = Budget(
            id = budgetId,
            income = BigDecimal("6500"),
            bankStart = BigDecimal("11000"),
            bankEnd = BigDecimal("8400"),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = budget

        viewModel = createViewModel(id = budgetId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(budget.income.toString(), uiState.income)
        assertEquals(budget.bankStart.toString(), uiState.bankStart)
        assertEquals(budget.bankEnd?.toString(), uiState.bankEnd)
        assertEquals(budget.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome("6300"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart("11200"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd("8600"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateDate(123L))
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)

        advanceUntilIdle()

        val expected = updateUseCase.updated
        val actual = listOf(budgetId, BigDecimal("6300"), BigDecimal("11200"), BigDecimal("8600"), 123L)
        assertEquals(expected, actual)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveBudget updates existing budget trimmed`() = runTest {
        val budgetId = "123"
        val budget = Budget(
            id = budgetId,
            income = BigDecimal("6500"),
            bankStart = BigDecimal("11000"),
            bankEnd = BigDecimal("8400"),
            date = 123L,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = budget

        viewModel = createViewModel(id = budgetId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(budget.income.toString(), uiState.income)
        assertEquals(budget.bankStart.toString(), uiState.bankStart)
        assertEquals(budget.bankEnd?.toString(), uiState.bankEnd)
        assertEquals(budget.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome(" 6300 "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart(" 11200 "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd(" 8600 "))
        viewModel.onIntent(AddEditBudgetIntent.UpdateDate(123L))
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)

        advanceUntilIdle()

        val expected = updateUseCase.updated
        val actual = listOf(budgetId, BigDecimal("6300"), BigDecimal("11200"), BigDecimal("8600"), 123L)
        assertEquals(expected, actual)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteBudget deletes existing Budget`() = runTest {
        val budgetId = "123"

        viewModel = createViewModel(id = budgetId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditBudgetIntent.DeleteBudget)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(budgetId, id)
    }

    @Test
    fun `deleteBudget deletes non-existing Budget`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditBudgetIntent.DeleteBudget)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `duplicateBudget creates new budget with current date`() = runTest {
        val budgetId = "123"
        val budget = Budget(
            id = budgetId,
            income = BigDecimal("5000"),
            bankStart = BigDecimal("3500"),
            bankEnd = BigDecimal("7500"),
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = budget

        viewModel = createViewModel(budgetId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditBudgetIntent.DuplicateBudget)

        advanceUntilIdle()

        val (income, bankStart, bankEnd, date) = createUseCase.created
        assertEquals(BigDecimal("5000"), income)
        assertEquals(BigDecimal("3500"), bankStart)
        assertEquals(BigDecimal("7500"), bankEnd)
        assertTrue((date as Long) > 123L)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateBudget with modified fields creates new budget`() = runTest {
        val budgetId = "456"
        val budget = Budget(
            id = budgetId,
            income = BigDecimal("5000"),
            bankStart = BigDecimal("3500"),
            bankEnd = BigDecimal("7500"),
            date = 123L,
            isDeleted = false,
            createdAt = 123L,
        )
        getUseCase.toReturn = budget

        viewModel = createViewModel(budgetId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditBudgetIntent.UpdateIncome("6000"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankStart("4500"))
        viewModel.onIntent(AddEditBudgetIntent.UpdateBankEnd("8500"))
        viewModel.onIntent(AddEditBudgetIntent.DuplicateBudget)

        advanceUntilIdle()

        val (income, bankStart, bankEnd, date) = createUseCase.created
        assertEquals(BigDecimal("6000"), income)
        assertEquals(BigDecimal("4500"), bankStart)
        assertEquals(BigDecimal("8500"), bankEnd)
        assertTrue((date as Long) > 123L)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateBudget without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditBudgetIntent.DuplicateBudget)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditBudgetIntent.SaveBudget)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditBudgetIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel(id: String?): AddEditBudgetViewModel {
        val route = AddEditBudgetRoute(id, if (id == null) R.string.add_budget else R.string.edit_budget)

        return AddEditBudgetViewModel(
            route,
            getUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }
}