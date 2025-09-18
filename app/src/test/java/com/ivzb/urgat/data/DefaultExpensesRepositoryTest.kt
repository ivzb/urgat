package com.ivzb.urgat.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.ivzb.urgat.data.local.database.Expenses
import com.ivzb.urgat.data.local.database.ExpensesDao

/**
 * Unit tests for [DefaultExpensesRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class) // TODO: Remove when stable
class DefaultExpensesRepositoryTest {

    @Test
    fun expensess_newItemSaved_itemIsReturned() = runTest {
        val repository = DefaultExpensesRepository(FakeExpensesDao())

        repository.add("Repository")

        assertEquals(repository.expensess.first().size, 1)
    }

}

private class FakeExpensesDao : ExpensesDao {

    private val data = mutableListOf<Expenses>()

    override fun getExpensess(): Flow<List<Expenses>> = flow {
        emit(data)
    }

    override suspend fun insertExpenses(item: Expenses) {
        data.add(0, item)
    }
}
