package com.ivzb.urgat.ui.addEditExpense

import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.domain.expense.FakeCreateExpenseUseCase
import com.ivzb.urgat.domain.expense.FakeDeleteExpenseUseCase
import com.ivzb.urgat.domain.expense.FakeGetExpenseUseCase
import com.ivzb.urgat.domain.expense.FakeGetLastUsedCurrencyUseCase
import com.ivzb.urgat.domain.expense.FakeUpdateExpenseUseCase
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
class AddEditExpenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getUseCase: FakeGetExpenseUseCase

    private lateinit var getLastUsedCurrencyUseCase: FakeGetLastUsedCurrencyUseCase

    private lateinit var createUseCase: FakeCreateExpenseUseCase

    private lateinit var updateUseCase: FakeUpdateExpenseUseCase

    private lateinit var deleteUseCase: FakeDeleteExpenseUseCase

    private lateinit var viewModel: AddEditExpenseViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getUseCase = FakeGetExpenseUseCase()
        getLastUsedCurrencyUseCase = FakeGetLastUsedCurrencyUseCase()
        createUseCase = FakeCreateExpenseUseCase()
        updateUseCase = FakeUpdateExpenseUseCase()
        deleteUseCase = FakeDeleteExpenseUseCase()

        viewModel = createViewModel(id = null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load expense`() = runTest {
        assertNull(getUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads expense`() = runTest {
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            currency = "EUR",
            category = "Transport",
            date = 123L,
            excludeFromAnalytics = false,
            isDeleted = false,
            createdAt = 1L,
        )

        getUseCase.toReturn = expense

        viewModel = createViewModel(id = expenseId)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Taxi", state.name)
        assertEquals("15", state.amount)
        assertEquals("Transport", state.category)
        assertEquals(123L, state.date)
        assertEquals(false, state.excludeFromAnalytics)
        assertEquals(false, state.isLoading)
        assertEquals(true, state.withDelete)
        assertEquals(false, state.shouldNavigateBack)
    }

    @Test
    fun `updateName updates uiState`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Lunch"))
        assertEquals("Lunch", viewModel.uiState.value.name)
    }

    @Test
    fun `saveExpense with empty fields shows message`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)
        assertEquals(R.string.empty_expense_message, viewModel.uiState.value.userMessage)
    }

    @Test
    fun `saveExpense with spaces shows message`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName(" "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount(" "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory(" "))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isEmpty())
        val uiState = viewModel.uiState.value
        assertEquals(R.string.empty_expense_message, uiState.userMessage)
        assertFalse(uiState.shouldNavigateBack)
    }

    @Test
    fun `saveExpense creates new expense`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Dinner"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("25"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCurrency("EUR"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory("Food"))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)
        
        advanceUntilIdle()

        val (name, amount, currency, category) = createUseCase.created
        assertEquals("Dinner", name)
        assertEquals(BigDecimal("25"), amount)
        assertEquals("EUR", currency)
        assertEquals("Food", category)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveExpense creates new expense trimmed`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName(" Dinner "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount(" 25 "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCurrency(" EUR "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory(" Food "))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        val (name, amount, currency, category) = createUseCase.created
        assertEquals("Dinner", name)
        assertEquals(BigDecimal("25"), amount)
        assertEquals("EUR", currency)
        assertEquals("Food", category)
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveExpense updates existing expense`() = runTest {
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            currency = "EUR",
            category = "Transport",
            date = 123L,
            excludeFromAnalytics = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = expense

        viewModel = createViewModel(expenseId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(expense.name, uiState.name)
        assertEquals(expense.amount.toString(), uiState.amount)
        assertEquals(expense.category, uiState.category)
        assertEquals(expense.date, uiState.date)
        assertEquals(expense.excludeFromAnalytics, uiState.excludeFromAnalytics)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Edit"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("35"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory("Math"))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        val (id, name, amount, currency, category) = updateUseCase.updated
        assertEquals(expenseId, id)
        assertEquals("Edit", name)
        assertEquals(BigDecimal("35"), amount)
        assertEquals("EUR", currency)
        assertEquals("Math", category)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `saveExpense updates existing expense trimmed`() = runTest {
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            currency = "EUR",
            category = "Transport",
            date = 123L,
            excludeFromAnalytics = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = expense

        viewModel = createViewModel(expenseId)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(expense.name, uiState.name)
        assertEquals(expense.amount.toString(), uiState.amount)
        assertEquals(expense.category, uiState.category)
        assertEquals(expense.date, uiState.date)
        assertEquals(expense.excludeFromAnalytics, uiState.excludeFromAnalytics)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditExpenseIntent.UpdateName(" Edit "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount(" 35 "))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory(" Math "))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        val (id, name, amount, currency, category) = updateUseCase.updated
        assertEquals(expenseId, id)
        assertEquals("Edit", name)
        assertEquals(BigDecimal("35"), amount)
        assertEquals("EUR", currency)
        assertEquals("Math", category)
        assertEquals(true, viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `deleteExpense deletes existing expense`() = runTest {
        val expenseId = "123"

        viewModel = createViewModel(expenseId)
        advanceUntilIdle()

        viewModel.onIntent(AddEditExpenseIntent.DeleteExpense)
        advanceUntilIdle()

        val (id) = deleteUseCase.deleted
        assertEquals(expenseId, id)
    }

    @Test
    fun `deleteExpense deletes non-existing expense`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditExpenseIntent.DeleteExpense)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
   fun `duplicateExpense resets state to create mode with current data`() = runTest {
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            currency = "EUR",
            category = "Transport",
            date = 123L,
            excludeFromAnalytics = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = expense

        viewModel = createViewModel(expenseId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditExpenseIntent.DuplicateExpense)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.add_expense, state.screenTitleResId)
        assertEquals("Taxi", state.name)
        assertEquals("15", state.amount)
        assertEquals("EUR", state.currency)
        assertEquals("Transport", state.category)
        assertTrue(state.date > 123L)
        assertEquals(false, state.excludeFromAnalytics)
        assertFalse(state.withDelete)
        assertFalse(state.withDuplicate)
        assertFalse(state.shouldNavigateBack)
    }

    @Test
    fun `duplicateExpense then save creates new expense`() = runTest {
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            currency = "EUR",
            category = "Transport",
            date = 123L,
            excludeFromAnalytics = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = expense

        viewModel = createViewModel(expenseId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditExpenseIntent.DuplicateExpense)

        advanceUntilIdle()

        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        assertTrue(createUseCase.created.isNotEmpty())
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(viewModel.uiState.value.shouldNavigateBack)
    }

    @Test
    fun `duplicateExpense with modified fields then save creates new expense`() = runTest {
        val expenseId = "456"
        val expense = Expense(
            id = expenseId,
            name = "Coffee",
            amount = BigDecimal("5"),
            currency = "EUR",
            category = "Food",
            date = 100L,
            excludeFromAnalytics = true,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = expense

        viewModel = createViewModel(expenseId)

        advanceUntilIdle()

        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Tea"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("3"))
        viewModel.onIntent(AddEditExpenseIntent.DuplicateExpense)

        advanceUntilIdle()

        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.add_expense, state.screenTitleResId)
        assertTrue(createUseCase.created.isNotEmpty())
        assertTrue(updateUseCase.updated.isEmpty())
        assertTrue(state.shouldNavigateBack)
    }

    @Test
    fun `duplicateExpense without id shows error`() = runTest {
        assertFailsWith<Exception> {
            viewModel.onIntent(AddEditExpenseIntent.DuplicateExpense)

            val uiState = viewModel.uiState.value
            assertEquals(R.string.something_went_wrong, uiState.userMessage)
        }
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditExpenseIntent.UserMessageShown)
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
    fun `init with id does not autofill last used currency`() = runTest {
        getLastUsedCurrencyUseCase.toReturn = "USD"
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            currency = "EUR",
            category = "Transport",
            date = 123L,
            excludeFromAnalytics = false,
            isDeleted = false,
            createdAt = 1L,
        )
        getUseCase.toReturn = expense

        viewModel = createViewModel(id = expenseId)

        advanceUntilIdle()

        assertEquals("EUR", viewModel.uiState.value.currency)
    }

    private fun createViewModel(id: String?): AddEditExpenseViewModel {
        val route = AddEditExpenseRoute(id, if (id == null) R.string.add_expense else R.string.edit_expense)

        return AddEditExpenseViewModel(
            route,
            getUseCase,
            getLastUsedCurrencyUseCase,
            createUseCase,
            updateUseCase,
            deleteUseCase,
        )
    }


}