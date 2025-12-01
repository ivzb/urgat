package com.ivzb.urgat.ui.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.domain.sample.SampleUseCase
import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo
import com.ivzb.urgat.util.Assert
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
class LibraryViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeNotesUseCase: ObserveNotesUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
    private val sampleUseCase: SampleUseCase,
) : ViewModel() {

    init {
        if (BuildConfig.WITH_SAMPLE_DATA) {
            viewModelScope.launch {
                sampleUseCase()
            }
        }
    }

    private val _uiEvent = MutableStateFlow<LibraryUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<LibraryUiState> = combine(
        observeTodosUseCase(_searchQuery, null, LIMIT),
        observeNotesUseCase(_searchQuery, null, LIMIT),
        _uiEvent,
    ) { todos, notes, uiEvent ->
        val items = listOf(todos, notes)

        when {
            items.any { it is Async.Loading } -> {
                LibraryUiState(isLoading = true)
            }

            items.any { it is Async.Error } -> {
                LibraryUiState(hasError = true)
            }

            items.all { it is Async.Success } -> {
                LibraryUiState(
                    headerItems = listOf(
                        HeaderItem(
                            LibraryHeaderItemId.Todo,
                            Icons.Filled.Done,
                            R.string.add_todo
                        ),
                        HeaderItem(
                            LibraryHeaderItemId.Note,
                            Icons.Filled.Person,
                            R.string.add_note
                        ),
                    ),
                    todos = (todos as Async.Success).data,
                    todosFilteringUiInfo = TodoFilteringUiInfo(
                        currentFilteringLabel = R.string.recent_todos,
                        emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_todos_label else R.string.no_todos_search,
                        emptyMessage = R.string.no_todos_message,
                        emptyButtonLabel = R.string.add_todo,
                    ),
                    notes = (notes as Async.Success).data,
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

            else -> {
                Assert.fail("library undefined state: $items")
                LibraryUiState(hasError = true)
            }
        }
    }
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
            is LibraryIntent.HeaderItemClick -> onHeaderItemClick(intent.itemId)
            is LibraryIntent.EventConsumed -> onEventConsumed()
        }
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

    private fun onHeaderItemClick(id: LibraryHeaderItemId) {
        viewModelScope.launch {
            _uiEvent.emit(
                when (id) {
                    LibraryHeaderItemId.Todo -> LibraryUiEvent.AddTodo
                    LibraryHeaderItemId.Note -> LibraryUiEvent.AddNote
                }
            )
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            _uiEvent.emit(null)
        }
    }

    companion object {

        private const val LIMIT = 3

    }

}