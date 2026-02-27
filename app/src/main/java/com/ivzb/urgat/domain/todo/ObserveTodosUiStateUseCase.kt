package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.todos.TodosUiState
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
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
                filteringUiInfo = TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_todos,
                    emptyLabel = if (searchQuery.isEmpty()) R.string.no_todos_label else R.string.no_todos_search,
                    emptyMessage = R.string.no_todos_message,
                    emptyButtonLabel = R.string.add_todo,
                    searchQuery = searchQuery,

                    filterMenuItems = filtering,
                    showFilterBadge = filterType != TodosFilterType.ALL_TODOS,
                ),
            )
        }
    ).map { Async.Success(it) }

}