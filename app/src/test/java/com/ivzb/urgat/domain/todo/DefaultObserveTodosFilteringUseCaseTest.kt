package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER_ID
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase.Companion.ACTIVE_FILTER_ID
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase.Companion.COMPLETED_FILTER_ID
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveTodosFilteringUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var observeUseCase: ObserveTodosFilteringUseCase
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        observeUseCase = DefaultObserveTodosFilteringUseCase(repository)
    }

    @Test
    fun `invoke should return Success with grouped items`() = runTest(testDispatcher) {
        val expected = listOf(
            FilterMenuItem(id = CLEAR_FILTER_ID, labelResId = R.string.filter_all, size = 4),
            FilterMenuItem(id = ACTIVE_FILTER_ID, labelResId = R.string.filter_active, size = 2),
            FilterMenuItem(id = COMPLETED_FILTER_ID, labelResId = R.string.filter_completed, size = 2),
        )

        val repositoryStream = flowOf(
            listOf(
                Todo("1", "title", "desc", isCompleted = false),
                Todo("2", "title2", "desc2", isCompleted = true),
                Todo("3", "title3", "desc3", isCompleted = false),
                Todo("4", "title4", "desc4", isCompleted = true),
            )
        )

        every { repository.getStream(NO_LIMIT) } returns repositoryStream

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Success)
        val items = (first as Async.Success).data
        assertEquals(expected, items)
    }

    @Test
    fun `invoke should return Error when repository throws`() = runTest(testDispatcher) {
        every { repository.getStream(NO_LIMIT) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
        assertEquals(R.string.error_todos_filtering, (first as Async.Error).errorMessage)
    }
}
