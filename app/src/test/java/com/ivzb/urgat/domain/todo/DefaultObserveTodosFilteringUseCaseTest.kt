package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
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
        val expected = listOf<TodosFilterMenuItem>(
            FilterMenuItem(type = TodosFilterType.ALL_TODOS, labelResId = R.string.filter_all, size = 4),
            FilterMenuItem(type = TodosFilterType.ACTIVE_TODOS, labelResId = R.string.filter_active, size = 2),
            FilterMenuItem(type = TodosFilterType.COMPLETED_TODOS, labelResId = R.string.filter_completed, size = 2),
        )

        val repositoryStream = flowOf(
            listOf(
                Todo("1", "title", isCompleted = false, false, 1L),
                Todo("2", "title2", isCompleted = true, false, 1L),
                Todo("3", "title3", isCompleted = false, false, 1L),
                Todo("4", "title4", isCompleted = true, false, 1L),
            )
        )

        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns repositoryStream

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
        every { repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE) } returns flow {
            throw RuntimeException("Test error")
        }

        val results = observeUseCase().toList()

        advanceUntilIdle()

        assertEquals(1, results.size)
        val first = results.first()
        assert(first is Async.Error)
    }
}
