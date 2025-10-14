package com.ivzb.urgat.ui.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.todo.TodosFilterType.*
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TodoUiState(
    val items: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: TodoFilteringUiInfo = TodoFilteringUiInfo()
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

    private val _searchQuery = MutableStateFlow("")

    private val _filteredTodosAsync =
        _searchQuery
            .debounce(DEBOUNCE_TIME_MILLIS)
            .flatMapLatest { searchQuery ->
                val todoStream = if (searchQuery == "") {
                    todoRepository.getTodosStream()
                } else {
                    todoRepository.getTodosStream(searchQuery = searchQuery)
                }

                combine(todoStream, _savedFilterType) { todos, type ->
                    filterTodos(todos, type)
                }
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

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

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

    private fun getFilterUiInfo(requestType: TodosFilterType): TodoFilteringUiInfo =
        when (requestType) {
            ALL_TODOS -> {
                TodoFilteringUiInfo(
                    currentFilteringLabel = R.string.all_todos,
                    emptyLabel = R.string.no_todos,
                    emptyIconRes = R.drawable.logo_no_fill
                )
            }

            ACTIVE_TODOS -> {
                TodoFilteringUiInfo(
                    currentFilteringLabel = R.string.active_todos,
                    emptyLabel = R.string.no_active_todos,
                    emptyIconRes = R.drawable.ic_check_circle
                )
            }

            COMPLETED_TODOS -> {
                TodoFilteringUiInfo(
                    currentFilteringLabel = R.string.completed_todos,
                    emptyLabel = R.string.no_completed_todos,
                    emptyIconRes = R.drawable.ic_verified_user
                )
            }
        }

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }

}

const val TODOS_FILTER_SAVED_STATE_KEY = "TODOS_FILTER_SAVED_STATE_KEY"

data class TodoFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_todos,

    override val emptyLabel: Int = R.string.no_todos,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_todos,
    override val loadingIconRes: Int = R.drawable.ic_loading,

    override val errorLabel: Int = R.string.error_todos,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo