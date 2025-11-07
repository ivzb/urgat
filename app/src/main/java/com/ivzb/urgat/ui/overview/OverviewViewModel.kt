package com.ivzb.urgat.ui.overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todo.TodoFilteringUiInfo
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeNotesUseCase: ObserveNotesUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val _uiEvent = MutableSharedFlow<OverviewUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val uiState: StateFlow<OverviewUiState> = combine(
        observeTodosUseCase(_searchQuery, null, LIMIT),
        observeExpensesUseCase(_searchQuery, null, LIMIT),
        observeNotesUseCase(_searchQuery, null, LIMIT),
    ) { todos, expenses, notes ->
        val items = listOf(todos, expenses, notes)

        when {
            items.any { it is Async.Loading } -> {
                OverviewUiState(isLoading = true)
            }

            items.any { it is Async.Error } -> {
                OverviewUiState(hasError = true)
            }

            items.all { it is Async.Success } -> {
                OverviewUiState(
                    headerItems = listOf(
                        HeaderItem(
                            OverviewHeaderItemId.Todo,
                            Icons.Filled.Done,
                            R.string.add_todo
                        ),
                        HeaderItem(
                            OverviewHeaderItemId.Expense,
                            Icons.Filled.ShoppingCart,
                            R.string.add_expense
                        ),
                        HeaderItem(
                            OverviewHeaderItemId.Note,
                            Icons.Filled.Person,
                            R.string.add_note
                        ),
                    ),
                    todoItems = (todos as Async.Success).data,
                    todoFilteringUiInfo = TodoFilteringUiInfo(
                        R.string.recent_todos,
                        if (_searchQuery.value.isEmpty()) R.string.no_todos_label else R.string.no_todos_search,
                        R.drawable.logo_no_fill,
                        R.string.no_todos_message,
                        R.string.add_todo,
                    ),
                    expensesItems = (expenses as Async.Success).data,
                    expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                        R.string.recent_expenses,
                        if (_searchQuery.value.isEmpty()) R.string.no_expenses_label else R.string.no_expenses_search,
                        R.drawable.logo_no_fill,
                        R.string.no_expenses_message,
                        R.string.add_expense,
                    ),
                    notesItems = (notes as Async.Success).data,
                    notesFilteringUiInfo = NotesFilteringUiInfo(
                        R.string.recent_notes,
                        if (_searchQuery.value.isEmpty()) R.string.no_notes_label else R.string.no_notes_search,
                        R.drawable.logo_no_fill,
                        R.string.no_notes_message,
                        R.string.add_note,
                    ),
                    isLoading = false,
                )
            }

            else -> throw Exception("undefined state: $todos, $expenses, $notes")
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = OverviewUiState(isLoading = true)
    )

    fun onIntent(intent: OverviewIntent) {
        when (intent) {
            OverviewIntent.ClearCompletedTodos -> clearCompletedTodos()
            is OverviewIntent.CompleteTodo -> completeTodo(intent.todo, intent.completed)
            is OverviewIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is OverviewIntent.HeaderItemClick -> onHeaderItemClick(intent.itemId)
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

    private fun onHeaderItemClick(id: OverviewHeaderItemId) {
        viewModelScope.launch {
            _uiEvent.emit(
                when (id) {
                    OverviewHeaderItemId.Todo -> OverviewUiEvent.AddTodo()
                    OverviewHeaderItemId.Expense -> OverviewUiEvent.AddExpense()
                    OverviewHeaderItemId.Note -> OverviewUiEvent.AddNote()
                }
            )
        }
    }

    companion object {

        private const val LIMIT = 3

    }

}