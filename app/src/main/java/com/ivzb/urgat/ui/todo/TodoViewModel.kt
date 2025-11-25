package com.ivzb.urgat.ui.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
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
class TodoViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeTodosFilteringUseCase: ObserveTodosFilteringUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(TODOS_FILTER_SAVED_STATE_KEY, null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TodoUiState> = combine(
        observeTodosUseCase(_searchQuery, _savedFilterType, NO_LIMIT),
        observeTodosFilteringUseCase(),
    ) { todosAsync, todosFilteringAsync ->

        val items = listOf(todosAsync, todosFilteringAsync)

        when {
            items.any { it is Async.Loading } -> {
                TodoUiState(
                    isLoading = true
                )
            }

            items.any { it is Async.Error } -> {
                TodoUiState(
                    hasError = true
                )
            }

            items.all { it is Async.Success } -> {
                TodoUiState(
                    items = (todosAsync as Async.Success<List<Todo>>).data,
                    isLoading = false,
                    filteringUiInfo = getFilterUiInfo(
                        filterMenuItems = (todosFilteringAsync as Async.Success<List<TodoFilterMenuItem>>).data
                    ),
                )
            }

            else -> error("undefined state: $items")
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TodoUiState(isLoading = true)
    )

    fun onIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is TodoIntent.SetFiltering -> setFiltering(intent.filter)
            is TodoIntent.ClearCompletedTodos -> clearCompletedTodos()
            is TodoIntent.CompleteTodo -> completeTodo(intent.todo, intent.completed)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filter: TodoFilterMenuItem) {
        savedStateHandle[TODOS_FILTER_SAVED_STATE_KEY] = filter
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

    private fun getFilterUiInfo(filterMenuItems: List<TodoFilterMenuItem>): TodoFilteringUiInfo {
        val filter: TodoFilterMenuItem? = savedStateHandle[TODOS_FILTER_SAVED_STATE_KEY]
        val filterId = filter?.type ?: TodoFilterType.ALL_TODOS

        return when (filterId) {
            TodoFilterType.ALL_TODOS -> {
                TodoFilteringUiInfo(
                    currentFilteringLabel = R.string.all_todos,
                    emptyLabel = R.string.no_todos,
                    filterMenuItems = filterMenuItems,
                )
            }

            TodoFilterType.ACTIVE_TODOS -> {
                TodoFilteringUiInfo(
                    currentFilteringLabel = R.string.active_todos,
                    emptyLabel = R.string.no_active_todos,
                    filterMenuItems = filterMenuItems,
                )
            }

            TodoFilterType.COMPLETED_TODOS -> {
                TodoFilteringUiInfo(
                    currentFilteringLabel = R.string.completed_todos,
                    emptyLabel = R.string.no_completed_todos,
                    filterMenuItems = filterMenuItems,
                )
            }
        }
    }
}

const val TODOS_FILTER_SAVED_STATE_KEY = "TODOS_FILTER_SAVED_STATE_KEY"