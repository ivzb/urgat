package com.ivzb.urgat.data.library

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.dataStoreEnumFlow
import com.ivzb.urgat.util.dataStoreStringFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultLibraryRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : LibraryRepository {

    override val searchQueryFlow: Flow<String> = dataStoreStringFlow(dataStore, SEARCH_QUERY, "")

    override suspend fun updateSearchQuery(value: String) {
        dataStore.edit { it[SEARCH_QUERY] = value }
    }

    override val todosFilterTypeFlow: Flow<TodosFilterType> = dataStoreEnumFlow(dataStore, TODOS_FILTER_TYPE, TodosFilterType.ALL_TODOS)

    override suspend fun updateTodosFilterType(value: TodosFilterType) {
        dataStore.edit { it[TODOS_FILTER_TYPE] = value.name }
    }

    companion object {

        private val SEARCH_QUERY = stringPreferencesKey("library_search_query")

        private val TODOS_FILTER_TYPE = stringPreferencesKey("library_todos_filter_type")

    }

}


