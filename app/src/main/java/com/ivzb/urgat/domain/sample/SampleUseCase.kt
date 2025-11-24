package com.ivzb.urgat.domain.sample

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.domain.expense.CreateExpenseUseCase
import com.ivzb.urgat.domain.investment.CreateInvestmentUseCase
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.todo.CreateTodoUseCase
import javax.inject.Inject

interface SampleUseCase {

    suspend operator fun invoke()

}

class DefaultSampleUseCase @Inject constructor(
    private val createTodoUseCase: CreateTodoUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val createInvestmentUseCase: CreateInvestmentUseCase,
): SampleUseCase {

    override suspend operator fun invoke() {
        if (!BuildConfig.DEBUG) {
            return
        }

        SAMPLE_TODOS.forEach { (title, description) ->
            createTodoUseCase(title, description)
        }

        SAMPLE_EXPENSES.forEach { (name, amount, category) ->
            createExpenseUseCase(name, amount.toBigDecimal(), category, System.currentTimeMillis())
        }

        SAMPLE_NOTES.forEach { (content) ->
            createNoteUseCase(content)
        }

        SAMPLE_INVESTMENTS.forEach { (name, currency, amount, category) ->
            createInvestmentUseCase(name, currency, amount.toBigDecimal(), category)
        }
    }

    companion object {

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
            listOf("Gym membership", "30", "Health"),
            listOf("Coffee", "4.75", "Food"),
            listOf("Uber ride", "18.40", "Transport"),
            listOf("Dinner at restaurant", "60", "Food"),
            listOf("Internet bill", "50", "Utilities"),
            listOf("Movie tickets", "25", "Entertainment"),
            listOf("Phone bill", "35", "Utilities"),
            listOf("Bus pass", "40", "Transport"),
            listOf("Clothing", "120", "Shopping"),
            listOf("Pet food", "25.50", "Pets"),
            listOf("Birthday gift", "80", "Gifts"),
            listOf("Salary", "-1500", "Job")
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
            listOf("Apple Inc.", "USD", "2000", "Stocks"),
            listOf("Microsoft Corp.", "USD", "1800", "Stocks"),
            listOf("Alphabet (Google)", "USD", "2200", "Stocks"),
            listOf("Amazon.com Inc.", "USD", "1700", "Stocks"),
            listOf("Tesla Inc.", "USD", "1900", "Stocks"),
            listOf("NVIDIA Corp.", "USD", "1600", "Stocks"),
            listOf("Meta Platforms", "USD", "1500", "Stocks"),
            listOf("Vanguard S&P 500 ETF", "USD", "3200", "ETF"),
            listOf("Bitcoin", "USD", "2500.50", "Cryptocurrency"),
            listOf("Ethereum", "USD", "850.75", "Cryptocurrency"),
            listOf("US Government Bonds", "USD", "2000", "Bonds"),
            listOf("Rental Property Deposit", "USD", "10000", "Real Estate"),
            listOf("Alibaba Group", "EUR", "3000", "Stocks"),
            listOf("Samsung Electronics", "EUR", "2400", "Stocks"),
            listOf("Toyota Motor Corp.", "EUR", "1500", "Stocks")
        )

    }

}
