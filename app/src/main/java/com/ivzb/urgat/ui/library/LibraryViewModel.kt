package com.ivzb.urgat.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.library.LibraryRepository
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.note.ObserveNotesUiStateUseCase
import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUiStateUseCase
import com.ivzb.urgat.ui.components.Page
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    observeTodosUiStateUseCase: ObserveTodosUiStateUseCase,
    observeNotesUiStateUseCase: ObserveNotesUiStateUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<LibraryUiEvent>()

    private val _searchQuery = libraryRepository.searchQueryFlow

    private val _savedTodosFilterType = libraryRepository.todosFilterTypeFlow

    val uiState: StateFlow<LibraryUiState> = combineUiState(
        observeTodosUiStateUseCase(_searchQuery, _savedTodosFilterType),
        observeNotesUiStateUseCase(_searchQuery),
        _searchQuery.map { Async.Success(it) },
        uiEventHolder(),

        onLoading = {
            LibraryUiState(isLoading = true)
        },
        onError = {
            LibraryUiState(hasError = true)
        },
        onSuccess = { todosUiState, notesUiState, searchQuery, uiEvent ->
            LibraryUiState(
                todosUiState = todosUiState,
                notesUiState = notesUiState,
                isLoading = false,
                filteringUiInfo = LibraryFilteringUiInfo(
                    searchQuery = searchQuery,
                ),
                uiEvent = uiEvent,
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = LibraryUiState(isLoading = true)
    )

    fun onIntent(intent: LibraryIntent) {
        when (intent) {
            LibraryIntent.ClearCompletedTodos -> clearCompletedTodos()
            is LibraryIntent.CompleteTodo -> completeTodo(intent.todo, intent.completed)
            is LibraryIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is LibraryIntent.SetTodosFiltering -> setTodosFiltering(intent.filterType)
            is LibraryIntent.ResetFilters -> resetFilters()
            is LibraryIntent.FabActionClick -> onFabActionClick(intent.page)
            is LibraryIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            libraryRepository.updateSearchQuery(newQuery)
        }
    }

    private fun setTodosFiltering(filterType: TodosFilterType) {
        viewModelScope.launch {
            libraryRepository.updateTodosFilterType(filterType)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setTodosFiltering(TodosFilterType.ALL_TODOS)
    }

    private fun completeTodo(todo: Todo, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            completeTodoUseCase(todo.id)
        } else {
            activateTodoUseCase(todo.id)
        }
    }

    private fun clearCompletedTodos() {
        viewModelScope.launch {
            clearCompletedTodosUseCase()
        }
    }

    private fun onFabActionClick(page: Page) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (page) {
                    LibraryPage.Todos -> LibraryUiEvent.AddTodo
                    LibraryPage.Notes -> LibraryUiEvent.AddNote
                    else -> null
                }
            )
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

}

const val LIBRARY_TODOS_FILTER_SAVED_STATE_KEY = "LIBRARY_TODOS_FILTER_SAVED_STATE_KEY"
