package com.ivzb.urgat.ui.addEditExpense

import androidx.lifecycle.SavedStateHandle
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expenses.FakeCreateExpenseUseCase
import com.ivzb.urgat.data.expenses.FakeGetExpenseUseCase
import com.ivzb.urgat.data.expenses.FakeUpdateExpenseUseCase
import com.ivzb.urgat.navigation.AddEditExpenseDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

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

        viewModel = AddEditExpenseViewModel(
            getExpenseUseCase,
            createExpenseUseCase,
            updateExpenseUseCase,
            savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateName updates uiState`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Lunch"))
        assertEquals("Lunch", viewModel.uiState.value.name)
    }

    @Test
    fun `saveExpense creates new expense`() = runTest {
        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Dinner"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("25"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory("Food"))

        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)
        
        testDispatcher.scheduler.advanceUntilIdle()

        val created = createExpenseUseCase.createdExpense
        assertEquals("Dinner", created?.first)
        assertEquals(BigDecimal("25"), created?.second)
        assertEquals("Food", created?.third)
        assertEquals(true, viewModel.uiState.value.isExpenseSaved)
    }

    @Test
    fun `saveExpense updates existing expense`() = runTest {
        val expenseId = "123"
        savedStateHandle[AddEditExpenseDestination.Args.ARG_ID] = expenseId

        viewModel = AddEditExpenseViewModel(
            getExpenseUseCase,
            createExpenseUseCase,
            updateExpenseUseCase,
            savedStateHandle
        )

        viewModel.onIntent(AddEditExpenseIntent.UpdateName("Book"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateAmount("30"))
        viewModel.onIntent(AddEditExpenseIntent.UpdateCategory("Education"))

        viewModel.onIntent(AddEditExpenseIntent.SaveExpense)

        advanceUntilIdle()

        val updated = updateExpenseUseCase.updatedExpense
        assertEquals(expenseId, updated?.first)
        assertEquals("Book", updated?.second)
        assertEquals(BigDecimal("30"), updated?.third)
        assertEquals("Education", updated?.fourth)
        assertEquals(true, viewModel.uiState.value.isExpenseSaved)
    }

    @Test
    fun `loadExpense populates uiState`() = runTest {
        val expenseId = "1"
        val expense = Expense(
            id = expenseId,
            name = "Taxi",
            amount = BigDecimal("15"),
            category = "Transport",
            date = 123L
        )

        getExpenseUseCase.expenseToReturn = expense
        savedStateHandle[AddEditExpenseDestination.Args.ARG_ID] = expenseId

        viewModel = AddEditExpenseViewModel(
            getExpenseUseCase,
            createExpenseUseCase,
            updateExpenseUseCase,
            savedStateHandle
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Taxi", state.name)
        assertEquals("15", state.amount)
        assertEquals("Transport", state.category)
        assertEquals(123L, state.date)
        assertEquals(false, state.isLoading)
    }
}