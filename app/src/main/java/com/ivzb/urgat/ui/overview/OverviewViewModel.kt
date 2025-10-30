package com.ivzb.urgat.ui.overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.useCase.ObserveExpensesUseCase
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.useCase.ObserveNotesUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.useCase.ActivateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.ClearCompletedTodosUseCase
import com.ivzb.urgat.data.todo.useCase.CompleteTodoUseCase
import com.ivzb.urgat.data.todo.useCase.ObserveTodosUseCase
import com.ivzb.urgat.ui.components.FilteringUiInfo
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

data class OverviewUiState(
    val headerItems: List<HeaderItem> = emptyList(),

    val todoItems: List<Todo> = emptyList(),
    val todoFilteringUiInfo: TodoFilteringUiInfo = TodoFilteringUiInfo(),

    val expensesItems: List<Expense> = emptyList(),
    val expensesFilteringUiInfo: ExpensesFilteringUiInfo = ExpensesFilteringUiInfo(),

    val notesItems: List<Note> = emptyList(),
    val notesFilteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo(),

    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: FilteringUiInfo = OverviewFilteringUiInfo()
)

sealed class UiEvent {
    class AddTodo() : UiEvent()
    class AddExpense() : UiEvent()
    class AddNote() : UiEvent()
}

@HiltViewModel
class OverviewViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeExpensesUseCase: ObserveExpensesUseCase,
    observeNotesUseCase: ObserveNotesUseCase,
    private val completeTodoUseCase: CompleteTodoUseCase,
    private val activateTodoUseCase: ActivateTodoUseCase,
    private val clearCompletedTodosUseCase: ClearCompletedTodosUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val uiState: StateFlow<OverviewUiState> = combine(
        _isLoading,
        observeTodosUseCase(_searchQuery, null, LIMIT),
        observeExpensesUseCase(_searchQuery, null, LIMIT),
        observeNotesUseCase(_searchQuery, null, LIMIT),
    ) { isLoading, todos, expenses, notes ->
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
                            HeaderItemId.Todo,
                            Icons.Filled.Done,
                            R.string.add_todo
                        ),
                        HeaderItem(
                            HeaderItemId.Expense,
                            Icons.Filled.ShoppingCart,
                            R.string.add_expense
                        ),
                        HeaderItem(
                            HeaderItemId.Note,
                            Icons.Filled.Person,
                            R.string.add_note
                        ),
                    ),
                    todoItems = (todos as Async.Success).data,
                    todoFilteringUiInfo = TodoFilteringUiInfo(
                        R.string.recent_todos,
                        if (_searchQuery.value.isEmpty()) R.string.no_todos else R.string.no_todos_search,
                        R.drawable.logo_no_fill
                    ),
                    expensesItems = (expenses as Async.Success).data,
                    expensesFilteringUiInfo = ExpensesFilteringUiInfo(
                        R.string.recent_expenses,
                        if (_searchQuery.value.isEmpty()) R.string.no_expenses else R.string.no_expenses_search,
                        R.drawable.logo_no_fill
                    ),
                    notesItems = (notes as Async.Success).data,
                    notesFilteringUiInfo = NotesFilteringUiInfo(
                        R.string.recent_notes,
                        if (_searchQuery.value.isEmpty()) R.string.no_notes else R.string.no_notes_search,
                        R.drawable.logo_no_fill
                    ),
                    isLoading = isLoading,
                )
            }

            else -> throw Exception("undefined state: $isLoading, $todos, $expenses")
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = OverviewUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun completeTodo(todo: Todo, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            completeTodoUseCase(todo.id)
        } else {
            activateTodoUseCase(todo.id)
        }
    }

    fun clearCompletedTodos() {
        viewModelScope.launch {
            clearCompletedTodosUseCase()
        }
    }

    fun onHeaderItemClick(item: HeaderItem) {
        viewModelScope.launch {
            _uiEvent.emit(
                when (item.id) {
                    HeaderItemId.Todo -> UiEvent.AddTodo()
                    HeaderItemId.Expense -> UiEvent.AddExpense()
                    HeaderItemId.Note -> UiEvent.AddNote()
                }
            )
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
) : FilteringUiInfo