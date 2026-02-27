package com.ivzb.urgat.data.library

import com.ivzb.urgat.ui.todos.TodosFilterType
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLibraryRepository : LibraryRepository {

    override val searchQueryFlow = MutableStateFlow("")
    override suspend fun updateSearchQuery(value: String) { searchQueryFlow.value = value }

    override val todosFilterTypeFlow = MutableStateFlow(TodosFilterType.ALL_TODOS)
    override suspend fun updateTodosFilterType(value: TodosFilterType) { todosFilterTypeFlow.value = value }

}


