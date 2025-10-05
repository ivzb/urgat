package com.ivzb.urgat.ui.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.todo.TodosFilterType.*
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoUiState(
    val items: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = FilteringUiInfo()
)

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType =
        savedStateHandle.getStateFlow(TODOS_FILTER_SAVED_STATE_KEY, ALL_TODOS)

    private val _filterUiInfo = _savedFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()
    private val _isLoading = MutableStateFlow(false)
    private val _filteredTodosAsync =
        combine(todoRepository.getTodosStream(), _savedFilterType) { todos, type ->
            filterTodos(todos, type)
        }
        .map { Async.Success(it) }
        .catch<Async<List<Todo>>> { emit(Async.Error(R.string.error_todos)) }

    val uiState: StateFlow<TodoUiState> = combine(
        _filterUiInfo, _isLoading, _filteredTodosAsync
    ) { filterUiInfo, isLoading, todosAsync ->
        when (todosAsync) {
            Async.Loading -> {
                TodoUiState(isLoading = true)
            }
            is Async.Error -> {
                TodoUiState(hasError = true)
            }
            is Async.Success -> {
                TodoUiState(
                    items = todosAsync.data,
                    filteringUiInfo = filterUiInfo,
                    isLoading = isLoading,
                )
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TodoUiState(isLoading = true)
    )

    fun setFiltering(requestType: TodosFilterType) {
        savedStateHandle[TODOS_FILTER_SAVED_STATE_KEY] = requestType
    }

    fun clearCompletedTodos() {
        viewModelScope.launch {
            todoRepository.clearCompletedTodos()
        }
    }

    fun completeTodo(todo: Todo, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            todoRepository.completeTodo(todo.id)
        } else {
            todoRepository.activateTodo(todo.id)
        }
    }

    private fun filterTodos(todos: List<Todo>, filteringType: TodosFilterType): List<Todo> {
        val todosToShow = ArrayList<Todo>()

        for (todo in todos) {
            when (filteringType) {
                ALL_TODOS -> todosToShow.add(todo)
                ACTIVE_TODOS -> if (todo.isActive) {
                    todosToShow.add(todo)
                }
                COMPLETED_TODOS -> if (todo.isCompleted) {
                    todosToShow.add(todo)
                }
            }
        }
        return todosToShow
    }

    private fun getFilterUiInfo(requestType: TodosFilterType): FilteringUiInfo =
        when (requestType) {
            ALL_TODOS -> {
                FilteringUiInfo(
                    R.string.all_todos, R.string.no_todos,
                    R.drawable.logo_no_fill
                )
            }
            ACTIVE_TODOS -> {
                FilteringUiInfo(
                    R.string.active_todos, R.string.no_active_todos,
                    R.drawable.ic_check_circle
                )
            }
            COMPLETED_TODOS -> {
                FilteringUiInfo(
                    R.string.completed_todos, R.string.no_completed_todos,
                    R.drawable.ic_verified_user
                )
            }
        }

}

const val TODOS_FILTER_SAVED_STATE_KEY = "TODOS_FILTER_SAVED_STATE_KEY"

data class FilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_todos,

    val noTodosLabel: Int = R.string.no_todos,
    val noTodoIconRes: Int = R.drawable.logo_no_fill,

    val loadingTodosLabel: Int = R.string.loading_todos,
    val loadingTodoIconRes: Int = R.drawable.ic_loading,

    val errorTodosLabel: Int = R.string.error_todos,
    val errorTodoIconRes: Int = R.drawable.ic_error,
)