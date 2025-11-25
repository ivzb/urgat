package com.ivzb.urgat.ui.overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.expense.ObserveExpensesUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.domain.sample.SampleUseCase
import com.ivzb.urgat.domain.todo.ActivateTodoUseCase
import com.ivzb.urgat.domain.todo.ClearCompletedTodosUseCase
import com.ivzb.urgat.domain.todo.CompleteTodoUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.ui.expenses.ExpensesFilteringUiInfo
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
class OverviewViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeNotesUseCase: ObserveNotesUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
    private val sampleUseCase: SampleUseCase,
) : ViewModel() {

    init {
        if (ADD_SAMPLE_DATA) {
            viewModelScope.launch {
                sampleUseCase()
            }
        }
    }

    private val _uiEvent = MutableStateFlow<OverviewUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<OverviewUiState> = combine(
        observeTodosUseCase(_searchQuery, null, LIMIT),
        observeExpensesUseCase(_searchQuery, null, LIMIT),
        observeNotesUseCase(_searchQuery, null, LIMIT),
        _uiEvent,
    ) { todos, expenses, notes, uiEvent ->
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
                        currentFilteringLabel = R.string.recent_todos,
                        emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_todos_label else R.string.no_todos_search,
                        emptyMessage = R.string.no_todos_message,
                        emptyButtonLabel = R.string.add_todo,
                    ),
                    expensesItems = (expenses as Async.Success).data,
                    expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                        currentFilteringLabel = R.string.recent_expenses,
                        emptyLabel = if (_searchQuery.value.isEmpty()) R.string.no_expenses_label else R.string.no_expenses_search,
                        emptyMessage = R.string.no_expenses_message,
                        emptyButtonLabel = R.string.add_expense,
                    ),
                    notesItems = (notes as Async.Success).data,
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
                Assert.fail("overview undefined state: $items")
                OverviewUiState(hasError = true)
            }
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
            is OverviewIntent.EventConsumed -> onEventConsumed()
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
                    OverviewHeaderItemId.Todo -> OverviewUiEvent.AddTodo
                    OverviewHeaderItemId.Expense -> OverviewUiEvent.AddExpense
                    OverviewHeaderItemId.Note -> OverviewUiEvent.AddNote
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

        private const val ADD_SAMPLE_DATA = false

    }

}