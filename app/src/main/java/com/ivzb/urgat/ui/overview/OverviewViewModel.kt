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
    private val createTodoUseCase: CreateTodoUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val createInvestmentUseCase: CreateInvestmentUseCase,
) : ViewModel() {

    init {
        if (ADD_SAMPLE_DATA) {
            viewModelScope.launch {
                SAMPLE_TODOS.map { (title, description) ->
                    createTodoUseCase(title, description)
                }

                SAMPLE_EXPENSES.map { (name, amount, category) ->
                    createExpenseUseCase(name, amount.toBigDecimal(), category, System.currentTimeMillis())
                }

                SAMPLE_NOTES.map { (content) ->
                    createNoteUseCase(content)
                }

                SAMPLE_INVESTMENTS.map { (name, currency, amount, category) ->
                    createInvestmentUseCase(name, currency, amount.toBigDecimal(), category)
                }
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

        private val SAMPLE_TODOS = listOf(
            listOf("Buy groceries", "Milk, eggs, bread, and fruits"),
            listOf("Read a book", "Finish reading 'Atomic Habits'"),
            listOf("Morning workout", "30 minutes of cardio and stretching"),
            listOf("Call plumber", "Fix leaking sink in the kitchen"),
            listOf("Plan weekend trip", "Decide on destination and book hotel"),
            listOf("Send work report", "Monthly sales report to manager"),
            listOf("Clean kitchen", "Wash dishes, wipe counters, sweep floor"),
            listOf("Update resume", "Add latest projects and experiences"),
            listOf("Meditation", "10 minutes of morning meditation"),
            listOf("Water plants", "Water all indoor and outdoor plants"),
            listOf("Prepare presentation", "Slides for Monday's team meeting"),
            listOf("Pay electricity bill", "Due by the end of the month"),
            listOf("Organize closet", "Sort clothes and donate unused items"),
            listOf("Write journal", "Reflect on today's achievements and challenges"),
            listOf("Grocery meal prep", "Cook meals for the next 3 days")
        )

        val SAMPLE_EXPENSES = listOf(
            listOf("Groceries", "75.50", "Food"),
            listOf("Netflix subscription", "12.99", "Entertainment"),
            listOf("Electricity bill", "45.20", "Utilities"),
            listOf("Gym membership", "30.00", "Health"),
            listOf("Coffee", "4.75", "Food"),
            listOf("Uber ride", "18.40", "Transport"),
            listOf("Dinner at restaurant", "60.00", "Food"),
            listOf("Internet bill", "50.00", "Utilities"),
            listOf("Movie tickets", "25.00", "Entertainment"),
            listOf("Phone bill", "35.00", "Utilities"),
            listOf("Bus pass", "40.00", "Transport"),
            listOf("Clothing", "120.00", "Shopping"),
            listOf("Pet food", "25.50", "Pets"),
            listOf("Birthday gift", "80.00", "Gifts"),
            listOf("Yoga class", "15.00", "Health")
        )

        val SAMPLE_NOTES = listOf(
            listOf("Meeting notes about Q4 goals and deadlines"),
            listOf(
                "Shopping list: eggs, milk, bread, coffee, yogurt, chicken, vegetables, pasta, rice, snacks. " +
                        "Also remember to buy cleaning supplies like dish soap and paper towels. " +
                        "Check if toiletries like toothpaste or shampoo need restocking."
            ),
            listOf("Idea: build a small habit-tracking Android app"),
            listOf(
                "Recipe: pasta with tomato sauce and basil. Cook pasta for 10 minutes, prepare sauce using fresh tomatoes, garlic, olive oil, and herbs. " +
                        "Let the sauce simmer for 20 minutes for richer flavor. Mix together, add parmesan, and serve warm."
            ),
            listOf("Workout plan: cardio, strength, yoga"),
            listOf(
                "Reminder: call mom in the evening. Ask her about the weekend plans and check if she needs help with anything. " +
                        "Also remind her about the upcoming family dinner next month so she can prepare in advance."
            ),
            listOf("Travel checklist: passport, charger, toiletries"),
            listOf(
                "Learning notes about Kotlin coroutines. Coroutines allow asynchronous programming without blocking threads. " +
                        "Key concepts: suspend functions, CoroutineScope, Dispatchers, and structured concurrency. " +
                        "Next topic to explore: exception handling and flow."
            ),
            listOf("Journal: today was productive and calm"),
            listOf(
                "Event planning: book venue, order cake, finalize guest list, prepare decorations. " +
                        "Consider adding a photo corner and small activity games. " +
                        "Check budget constraints and update costs after booking vendors."
            ),
            listOf("Photography ideas: street portraits at night"),
            listOf(
                "Brainstorming: new features for finance app. Ideas include smart categorization using ML, spending insights, export to PDF, " +
                        "sync across devices with cloud backup, recurring expense detection, and personalized budgeting recommendations."
            ),
            listOf("Gardening tips: water early morning"),
            listOf(
                "Creative writing prompt: write a story set in a forgotten library where the books whisper secrets at night. " +
                        "Develop a main character who discovers a hidden section of enchanted manuscripts. " +
                        "Focus on setting atmosphere, mystery, and emotional growth."
            ),
            listOf("Long-term goals list for next year")
        )

        val SAMPLE_INVESTMENTS = listOf(
            listOf("Apple Inc.", "USD", "2000.00", "Stocks"),
            listOf("Microsoft Corp.", "USD", "1800.00", "Stocks"),
            listOf("Alphabet (Google)", "USD", "2200.00", "Stocks"),
            listOf("Amazon.com Inc.", "USD", "1700.00", "Stocks"),
            listOf("Tesla Inc.", "USD", "1900.00", "Stocks"),
            listOf("NVIDIA Corp.", "USD", "1600.00", "Stocks"),
            listOf("Meta Platforms", "USD", "1500.00", "Stocks"),
            listOf("Vanguard S&P 500 ETF", "USD", "3200.00", "ETF"),
            listOf("Bitcoin", "USD", "2500.50", "Cryptocurrency"),
            listOf("Ethereum", "USD", "850.75", "Cryptocurrency"),
            listOf("US Government Bonds", "USD", "2000.00", "Bonds"),
            listOf("Rental Property Deposit", "USD", "10000.00", "Real Estate"),
            listOf("Alibaba Group", "HKD", "3000.00", "Stocks"),
            listOf("Samsung Electronics", "KRW", "2400000", "Stocks"),
            listOf("Toyota Motor Corp.", "JPY", "150000", "Stocks")
        )

    }

}