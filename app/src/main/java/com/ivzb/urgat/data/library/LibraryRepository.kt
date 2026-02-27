package com.ivzb.urgat.data.library

import com.ivzb.urgat.ui.todos.TodosFilterType
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    val searchQueryFlow: Flow<String>
    suspend fun updateSearchQuery(value: String)

    val todosFilterTypeFlow: Flow<TodosFilterType>
    suspend fun updateTodosFilterType(value: TodosFilterType)

}

