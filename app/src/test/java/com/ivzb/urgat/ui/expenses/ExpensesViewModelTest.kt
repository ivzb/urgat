package com.ivzb.urgat.ui.expenses


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.ivzb.urgat.data.expense.ExpensesRepository

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class ExpensesViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = ExpensesViewModel(FakeExpensesRepository())
        assertEquals(viewModel.uiState.first(), ExpensesUiState.Loading)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = ExpensesViewModel(FakeExpensesRepository())
        assertEquals(viewModel.uiState.first(), ExpensesUiState.Loading)
    }
}

private class FakeExpensesRepository : ExpensesRepository {

    private val data = mutableListOf<String>()

    override val expenses: Flow<List<String>>
        get() = flow { emit(data.toList()) }

    override suspend fun add(name: String) {
        data.add(0, name)
    }
}
