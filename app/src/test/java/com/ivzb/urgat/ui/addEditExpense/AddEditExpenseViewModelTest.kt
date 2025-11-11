package com.ivzb.urgat.ui.addEditExpense

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.domain.expense.FakeCreateExpenseUseCase
import com.ivzb.urgat.domain.expense.FakeGetExpenseUseCase
import com.ivzb.urgat.domain.expense.FakeUpdateExpenseUseCase
import com.ivzb.urgat.navigation.AddEditExpenseDestination
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
class AddEditExpenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getExpenseUseCase: FakeGetExpenseUseCase

    private lateinit var createExpenseUseCase: FakeCreateExpenseUseCase

    private lateinit var updateExpenseUseCase: FakeUpdateExpenseUseCase

    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: AddEditExpenseViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getExpenseUseCase = FakeGetExpenseUseCase()
        createExpenseUseCase = FakeCreateExpenseUseCase()
        updateExpenseUseCase = FakeUpdateExpenseUseCase()
        savedStateHandle = SavedStateHandle()

        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init without id does not load expense`() = runTest {
        assertNull(getExpenseUseCase.calledWithId)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `init with id loads expense`() = runTest {
        val expenseId = "123"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            category = "Transport",
            date = 123L
        )

        getExpenseUseCase.toReturn = expense
        savedStateHandle[AddEditExpenseDestination.Args.ARG_ID] = expenseId

        viewModel = createViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Taxi", state.name)
        assertEquals("15", state.amount)
        assertEquals("Transport", state.category)
        assertEquals(123L, state.date)
        assertEquals(false, state.isLoading)
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
    fun `saveExpense creates new expense`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Dinner"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("25"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory("Food"))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)
        
        advanceUntilIdle()

        val created = createExpenseUseCase.created
        assertEquals("Dinner", created?.first)
        assertEquals(BigDecimal("25"), created?.second)
        assertEquals("Food", created?.third)
        assertTrue(viewModel.uiState.value.isExpenseSaved)
    }

    @Test
    fun `saveExpense updates existing expense`() = runTest {
        val expenseId = "123"
        savedStateHandle[AddEditExpenseDestination.Args.ARG_ID] = expenseId

        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            category = "Transport",
            date = 123L
        )
        getExpenseUseCase.toReturn = expense

        viewModel = createViewModel()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(expense.name, uiState.name)
        assertEquals(expense.amount.toString(), uiState.amount)
        assertEquals(expense.category, uiState.category)
        assertEquals(expense.date, uiState.date)
        assertFalse(uiState.isLoading)

        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Edit"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("35"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory("Math"))
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        val updated = updateExpenseUseCase.updated
        assertEquals(expenseId, updated?.first)
        assertEquals("Edit", updated?.second)
        assertEquals(BigDecimal("35"), updated?.third)
        assertEquals("Math", updated?.fourth)
        assertEquals(true, viewModel.uiState.value.isExpenseSaved)
    }

    @Test
    fun `userMessageShown clears message`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)
        assertNotNull(viewModel.uiState.value.userMessage)

        viewModel.onIntent(AddEditExpenseIntent.UserMessageShown)
        assertNull(viewModel.uiState.value.userMessage)
    }

    private fun createViewModel() = AddEditExpenseViewModel(
        getExpenseUseCase,
        createExpenseUseCase,
        updateExpenseUseCase,
        savedStateHandle
    )
}