package com.ivzb.urgat.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OverviewUiState(
    val todoItems: List<Todo> = emptyList(),
    val todoFilteringUiInfo: TodoFilteringUiInfo = TodoFilteringUiInfo(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = OverviewFilteringUiInfo()
)

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _todosAsync =
        todoRepository.getTodosStream(LIMIT)
            .map { Async.Success(it) }
            .catch<Async<List<Todo>>> { emit(Async.Error(R.string.error_todos)) }

    val uiState: StateFlow<OverviewUiState> = combine(
        _isLoading, _todosAsync
    ) { isLoading, todosAsync ->
        when (todosAsync) {
            Async.Loading -> {
                OverviewUiState(isLoading = true)
            }

            is Async.Error -> {
                OverviewUiState(hasError = true)
            }

            is Async.Success -> {
                OverviewUiState(
                    todoItems = todosAsync.data,
                    todoFilteringUiInfo = TodoFilteringUiInfo(
                        R.string.recent_todos,
                        R.string.no_todos,
                        R.drawable.logo_no_fill
                    ),
                    isLoading = isLoading,
                )
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = OverviewUiState(isLoading = true)
    )

    fun completeTodo(todo: Todo, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            todoRepository.completeTodo(todo.id)
        } else {
            todoRepository.activateTodo(todo.id)
        }
    }

    companion object {

        private const val LIMIT = 3
    }

}

data class OverviewFilteringUiInfo(
    override val emptyLabel: Int = 0,
    override val emptyIconRes: Int = 0,

    override val loadingLabel: Int = R.string.loading_overview,
    override val loadingIconRes: Int = R.drawable.ic_loading,

    override val errorLabel: Int = R.string.error_overview,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo {

}