package com.ivzb.urgat.ui.todos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosFilteringUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodosViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeTodosFilteringUseCase: ObserveTodosFilteringUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(TODOS_FILTER_SAVED_STATE_KEY, TodosFilterType.ALL_TODOS)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TodosUiState> = combine(
        observeTodosUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeTodosFilteringUseCase(),
    ) { todosAsync, todosFilteringAsync ->
        when {
            todosAsync is Async.Loading || todosFilteringAsync is Async.Loading -> {
                TodosUiState(
                    isLoading = true
                )
            }

            todosAsync is Async.Error || todosFilteringAsync is Async.Error -> {
                TodosUiState(
                    hasError = true
                )
            }

            todosAsync is Async.Success && todosFilteringAsync is Async.Success -> {
                TodosUiState(
                    items = todosAsync.data,
                    isLoading = false,
                    filteringUiInfo = getFilterUiInfo(
                        filterMenuItems = todosFilteringAsync.data
                    ),
                )
            }

            else -> error("undefined state: $todosAsync, $todosFilteringAsync")
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TodosUiState(isLoading = true)
    )

    fun onIntent(intent: TodosIntent) {
        when (intent) {
            is TodosIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is TodosIntent.SetFiltering -> setFiltering(intent.filterType)
            is TodosIntent.ClearCompletedTodos -> clearCompletedTodos()
            is TodosIntent.CompleteTodos -> completeTodo(intent.todo, intent.completed)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: TodosFilterType) {
        savedStateHandle[TODOS_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun clearCompletedTodos() {
        viewModelScope.launch {
            clearCompletedTodosUseCase()
        }
    }

    private fun completeTodo(todo: Todo, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            completeTodoUseCase(todo.id)
        } else {
            activateTodoUseCase(todo.id)
        }
    }

    private fun getFilterUiInfo(filterMenuItems: List<TodosFilterMenuItem>): TodosFilteringUiInfo {
        val filterType: TodosFilterType = savedStateHandle[TODOS_FILTER_SAVED_STATE_KEY] ?: TodosFilterType.ALL_TODOS

        return when (filterType) {
            TodosFilterType.ALL_TODOS -> {
                TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.all_todos,
                    emptyLabel = R.string.no_todos,
                    filterMenuItems = filterMenuItems,
                )
            }

            TodosFilterType.ACTIVE_TODOS -> {
                TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.active_todos,
                    emptyLabel = R.string.no_active_todos,
                    filterMenuItems = filterMenuItems,
                )
            }

            TodosFilterType.COMPLETED_TODOS -> {
                TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.completed_todos,
                    emptyLabel = R.string.no_completed_todos,
                    filterMenuItems = filterMenuItems,
                )
            }
        }
    }
}

const val TODOS_FILTER_SAVED_STATE_KEY = "TODOS_FILTER_SAVED_STATE_KEY"