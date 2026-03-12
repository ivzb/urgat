package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveTodosUiStateUseCaseTest {

    private lateinit var fakeObserveTodosUseCase: FakeObserveTodosUseCase
    private lateinit var fakeObserveTodosFilteringUseCase: FakeObserveTodosFilteringUseCase

    private lateinit var useCase: ObserveTodosUiStateUseCase

    @Before
    fun setUp() {
        fakeObserveTodosUseCase = FakeObserveTodosUseCase()
        fakeObserveTodosFilteringUseCase = FakeObserveTodosFilteringUseCase()

        useCase = DefaultObserveTodosUiStateUseCase(
            fakeObserveTodosUseCase,
            fakeObserveTodosFilteringUseCase,
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Loading)
        fakeObserveTodosFilteringUseCase.emit(Async.Loading)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(TodosFilterType.ALL_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.isLoading)
        assertFalse(uiState.hasError)
        assertTrue(uiState.todos.isEmpty())
    }

    @Test
    fun `emits success state when use case returns data`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(TodosFilterType.ALL_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeTodos, uiState.todos)
        assertEquals(fakeTodosFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits success state when use case returns data with filter badge`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(TodosFilterType.ACTIVE_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertEquals(fakeTodos, uiState.todos)
        assertEquals(fakeTodosFiltering, uiState.filteringUiInfo.filterMenuItems)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.hasError)
    }

    @Test
    fun `emits error state when use case returns error`() = runTest {
        fakeObserveTodosUseCase.emit(Async.Error)
        fakeObserveTodosFilteringUseCase.emit(Async.Error)

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(TodosFilterType.ALL_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.hasError)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `hasAppliedFilters is false when no filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(TodosFilterType.ALL_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertFalse(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(R.string.no_todos, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_todos_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.add_todo, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when search query is not empty`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("lunch")
        val filterTypeFlow = flowOf(TodosFilterType.ALL_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("lunch", uiState.filteringUiInfo.searchQuery)
        assertFalse(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(R.string.no_todos_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_todos_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when filter type is applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("")
        val filterTypeFlow = flowOf(TodosFilterType.ACTIVE_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(R.string.no_todos_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_todos_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    @Test
    fun `hasAppliedFilters is true when all filters are applied`() = runTest {
        emitSuccessData()

        val searchQueryFlow = flowOf("lunch")
        val filterTypeFlow = flowOf(TodosFilterType.COMPLETED_TODOS)

        val results = useCase(searchQueryFlow, filterTypeFlow).first()

        advanceUntilIdle()

        assertTrue(results is Async.Success)
        val uiState = results.data
        assertTrue(uiState.filteringUiInfo.hasAppliedFilters)
        assertEquals("lunch", uiState.filteringUiInfo.searchQuery)
        assertTrue(uiState.filteringUiInfo.showFilterBadge)
        assertEquals(R.string.no_todos_search, uiState.filteringUiInfo.emptyLabel)
        assertEquals(R.string.no_todos_search_message, uiState.filteringUiInfo.emptyMessage)
        assertEquals(R.string.reset_filters, uiState.filteringUiInfo.emptyButtonLabel)
    }

    private fun emitSuccessData() {
        fakeObserveTodosUseCase.emit(Async.Success(fakeTodos))
        fakeObserveTodosFilteringUseCase.emit(Async.Success(fakeTodosFiltering))
    }

    companion object {
        private val fakeTodos = listOf(
            Todo("1", "Lunch", false, false, 1L)
        )

        private val fakeTodosFiltering = listOf(
            FilterMenuItem(type = TodosFilterType.ALL_TODOS, labelResId = R.string.all_todos, size = 1),
            FilterMenuItem(type = TodosFilterType.ACTIVE_TODOS, labelResId = R.string.active_todos, size = 1),
            FilterMenuItem(type = TodosFilterType.COMPLETED_TODOS, labelResId = R.string.completed_todos, size = 0),
        )
    }

}

