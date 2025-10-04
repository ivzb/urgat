package com.ivzb.urgat.data

import com.ivzb.urgat.data.expense.DefaultExpensesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseDao

/**
 * Unit tests for [DefaultExpensesRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultExpensesRepositoryTest {

    @Test
    fun expensess_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultExpensesRepository(FakeExpenseDao())

        repository.add("Repository")

        assertEquals(repository.expenses.first().size, 1)
    }

}

private class FakeExpenseDao : ExpenseDao {

    private val data = mutableListOf<Expense>()

    override fun getExpenses(): Flow<List<Expense>> = flow {
        emit(data)
    }

    override suspend fun insertExpense(item: Expense) {
        data.add(0, item)
    }
}
