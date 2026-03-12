package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.todos.TodosFilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveTodosUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodosFilterType>,
    ): Flow<Async<TodosUiState>>

}

class DefaultObserveTodosUiStateUseCase @Inject constructor(
    private val observeTodosUseCase: ObserveTodosUseCase,
    private val observeTodosFilteringUseCase: ObserveTodosFilteringUseCase,
): ObserveTodosUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodosFilterType>,
    ): Flow<Async<TodosUiState>> = combineUiState(
        observeTodosUseCase(searchQueryFlow, null, filterTypeFlow, null, null),
        observeTodosFilteringUseCase(),
        combine(searchQueryFlow, filterTypeFlow, ::Pair).map { Async.Success(it) },

        onLoading = {
            TodosUiState(isLoading = true)
        },
        onError = {
            TodosUiState(hasError = true)
        },
        onSuccess = { todos, filtering, (searchQuery, filterType) ->
            TodosUiState(
                todos = todos,
                isLoading = false,
                filteringUiInfo = getFilterUiInfo(
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterMenuItems = filtering,
                ),
            )
        }
    ).map { Async.Success(it) }

    private fun getFilterUiInfo(
        searchQuery: String,
        filterType: TodosFilterType,
        filterMenuItems: List<TodosFilterMenuItem>,
    ): TodosFilteringUiInfo {
        val showFilterBadge = filterType != TodosFilterType.ALL_TODOS
        val hasAppliedFilters = searchQuery.isNotEmpty() || showFilterBadge

        return TodosFilteringUiInfo(
            currentFilteringLabel = R.string.all_todos,

            emptyLabel = when {
                hasAppliedFilters -> R.string.no_todos_search
                else -> R.string.no_todos
            },
            emptyMessage = when {
                hasAppliedFilters -> R.string.no_todos_search_message
                else -> R.string.no_todos_message
            },
            emptyButtonLabel = when {
                hasAppliedFilters -> R.string.reset_filters
                else -> R.string.add_todo
            },

            searchQuery = searchQuery,
            hasAppliedFilters = hasAppliedFilters,

            filterMenuItems = filterMenuItems,
            showFilterBadge = showFilterBadge,
        )
    }

}