package com.ivzb.urgat.data.library

import com.ivzb.urgat.data.dataStore.FakePreferencesDataStore
import com.ivzb.urgat.ui.todos.TodosFilterType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultLibraryRepositoryTest {

    private lateinit var dataStore: FakePreferencesDataStore
    private lateinit var repository: DefaultLibraryRepository

    @Before
    fun setup() {
        dataStore = FakePreferencesDataStore()
        repository = DefaultLibraryRepository(dataStore)
    }

    @Test
    fun `searchQueryFlow emits default value when not set`() = runTest {
        assertEquals("", repository.searchQueryFlow.first())
    }

    @Test
    fun `updateSearchQuery updates flow value`() = runTest {
        repository.updateSearchQuery("coffee")
        assertEquals("coffee", repository.searchQueryFlow.first())
    }

    @Test
    fun `todosFilterTypeFlow emits default value when not set`() = runTest {
        assertEquals(TodosFilterType.ALL_TODOS, repository.todosFilterTypeFlow.first())
    }

    @Test
    fun `updateTodosFilterType updates flow value`() = runTest {
        repository.updateTodosFilterType(TodosFilterType.COMPLETED_TODOS)
        assertEquals(TodosFilterType.COMPLETED_TODOS, repository.todosFilterTypeFlow.first())
    }

}

