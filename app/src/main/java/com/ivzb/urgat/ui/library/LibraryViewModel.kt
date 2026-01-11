package com.ivzb.urgat.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.R
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeNotesUseCase: ObserveNotesUseCase,
    resourcesProvider: ResourcesProvider,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<LibraryUiEvent>()

    private val _searchQuery = MutableStateFlow("")

    private val _itemsLimit = MutableStateFlow(resourcesProvider.getInteger(R.integer.items_limit))

    val uiState: StateFlow<LibraryUiState> = combineUiState(
        observeTodosUseCase(_searchQuery, null, _itemsLimit),
        observeNotesUseCase(_searchQuery, null, _itemsLimit),
        uiEventHolder(),

        onLoading = {
            LibraryUiState(isLoading = true)
        },
        onError = {
            LibraryUiState(hasError = true)
        },
        onSuccess = { todos, notes, uiEvent ->
            LibraryUiState(
                todos = todos,
                todosFilteringUiInfo = TodosFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_todos,
                    emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_todos_label else R.string.no_todos_search,
                    emptyMessage = R.string.no_todos_message,
                    emptyButtonLabel = R.string.add_todo,
                ),
                notes = notes,
                notesFilteringUiInfo = NotesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_notes,
                    emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_notes_label else R.string.no_notes_search,
                    emptyMessage = R.string.no_notes_message,
                    emptyButtonLabel = R.string.add_note,
                ),
                isLoading = false,
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
            is LibraryIntent.ItemsLimitChanged -> onItemsLimitChanged(intent.itemsLimit)
            LibraryIntent.ClearCompletedTodos -> clearCompletedTodos()
            is LibraryIntent.CompleteTodo -> completeTodo(intent.todo, intent.completed)
            is LibraryIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is LibraryIntent.FabActionClick -> onFabActionClick(intent.itemId)
            is LibraryIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onItemsLimitChanged(newLimit: Int) {
        _itemsLimit.value = newLimit
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
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

    private fun onFabActionClick(id: LibraryFabActionId) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (id) {
                    LibraryFabActionId.Todo -> LibraryUiEvent.AddTodo
                    LibraryFabActionId.Note -> LibraryUiEvent.AddNote
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