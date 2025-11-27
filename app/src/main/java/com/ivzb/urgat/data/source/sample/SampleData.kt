package com.ivzb.urgat.data.source.sample

import com.ivzb.urgat.data.investment.Investment
import kotlin.collections.component1

object SampleData {

    val SAMPLE_TODOS = listOf(
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
        listOf("Apple Inc.", "2000", "276.97", "Stocks", "USD"),
        listOf("Microsoft Corp.", "1800", "485.5", "Stocks", "USD"),
        listOf("Alphabet (Google)", "2200", "323.44", "Stocks", "USD"),
        listOf("Amazon.com Inc.", "1700", "229.16", "Stocks", "USD"),
        listOf("Tesla Inc.", "1900", "419.40", "Stocks", "USD"),
        listOf("NVIDIA Corp.", "1600", "180.26", "Stocks", "USD"),
        listOf("Meta Platforms", "1500", "636.22", "Stocks", "USD"),
        listOf("Vanguard S&P 500 ETF", "3200", "621.88", "ETF", "USD"),
        listOf("Bitcoin", "2500.50", "90966", "Cryptocurrency", "USD"),
        listOf("Ethereum", "850.75", "3021", "Cryptocurrency", "USD"),
        listOf("US Government Bonds", "2000", "2000.00", "Bonds", "USD"),
        listOf("Rental Property Deposit", "10000", "10000.00", "Real Estate", "USD"),
        listOf("Alibaba Group", "3000", "133", "Stocks", "EUR"),
        listOf("Samsung Electronics", "2400", "1128", "Stocks", "EUR"),
        listOf("Toyota Motor Corp.", "1500", "210", "Stocks", "EUR")
    )
    .mapIndexed { index, (name, amount, cost, category, currency) ->
        Investment(
            id = index.toString(),
            name = name,
            amount = amount.toBigDecimal(),
            cost = cost.toBigDecimal(),
            category = category,
            currency = currency,
            date = System.currentTimeMillis(),
            isDeleted = false,
            createdAt = System.currentTimeMillis(),
        )
    }

}