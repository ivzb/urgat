package com.ivzb.urgat.data.source.sample

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.common.Group
import kotlin.collections.component1
import kotlin.collections.component4

object SampleData {

    val SAMPLE_TODOS = debugOnly {
        listOf(
            listOf("Morning workout", "30 minutes of cardio and stretching", "true"),
            listOf("Buy groceries", "Milk, eggs, bread, and fruits", "false"),
            listOf("Read a book", "Finish reading 'Atomic Habits'", "false"),
            listOf("Call plumber", "Fix leaking sink in the kitchen", "false"),
            listOf("Plan weekend trip", "Decide on destination and book hotel", "false"),
            listOf("Send work report", "Monthly sales report to manager", "true"),
            listOf("Clean kitchen", "Wash dishes, wipe counters, sweep floor", "false"),
            listOf("Update resume", "Add latest projects and experiences", "false"),
            listOf("Meditation", "10 minutes of morning meditation", "true"),
            listOf("Water plants", "Water all indoor and outdoor plants", "false"),
            listOf("Prepare presentation", "Slides for Monday's team meeting", "false"),
            listOf("Pay electricity bill", "Due by the end of the month", "true"),
            listOf("Organize closet", "Sort clothes and donate unused items", "false"),
            listOf("Write journal", "Reflect on today's achievements and challenges", "false"),
            listOf("Grocery meal prep", "Cook meals for the next 3 days", "true")
        )
    }.mapIndexed { index, (title, description, isCompleted) ->
        Todo(
            id = index.toString(),
            title = title,
            description = description,
            isCompleted = isCompleted.toBoolean(),
            isDeleted = false,
            createdAt = System.currentTimeMillis(),
        )
    }

    val SAMPLE_EXPENSES = debugOnly {
        listOf(
            listOf("Groceries", "75.50", "Food", "6048000"),
            listOf("Clothing", "120", "Shopping", "4752000"),
            listOf("Netflix subscription", "12.99", "Entertainment", "4752000"),
            listOf("Electricity bill", "45.20", "Utilities", "3024000"),
            listOf("Gym membership", "30", "Health", "3024000"),
            listOf("Coffee", "4.75", "Food", "2160000"),
            listOf("Uber ride", "18.40", "Transport", "2160000"),
            listOf("Dinner at restaurant", "60", "Food", "1296000"),
            listOf("Internet bill", "50", "Utilities", "1296000"),
            listOf("Movie tickets", "25", "Entertainment", "864000"),
            listOf("Phone bill", "35", "Utilities", "864000"),
            listOf("Bus pass", "40", "Transport", "432000"),
            listOf("Salary", "-1500", "Job", "432000"),
            listOf("Pet food", "25.50", "Pets", "0"),
            listOf("Birthday gift", "80", "Gifts", "0"),
        )
    }.mapIndexed { index, (name, amount, category, dateOffset) ->
        Expense(
            id = index.toString(),
            name = name,
            amount = amount.toBigDecimal(),
            category = category,
            date = System.currentTimeMillis() - (dateOffset.toLong() * 1_000),
            isDeleted = false,
            createdAt = System.currentTimeMillis() - (dateOffset.toLong() * 1_000),
        )
    }

    val SAMPLE_EXPENSES_GROUPED = Group.byDate(SAMPLE_EXPENSES, Expense::date, Expense::total)

    val SAMPLE_EXPENSES_CATEGORIES = Group.groupExpenseCategories(SAMPLE_EXPENSES)

    val SAMPLE_NOTES = debugOnly {
        listOf(
            listOf(
                "Shopping list: eggs, milk, bread, coffee, yogurt, chicken, vegetables, pasta, rice, snacks. " +
                        "Also remember to buy cleaning supplies like dish soap and paper towels. " +
                        "Check if toiletries like toothpaste or shampoo need restocking."
            ),
            listOf("Meeting notes about Q4 goals and deadlines"),
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
            listOf("Long-term goals list for next year"),
        )
    }.mapIndexed { index, (content) ->
        Note(
            id = index.toString(),
            content = content,
            isDeleted = false,
            createdAt = System.currentTimeMillis(),
        )
    }

    val SAMPLE_INVESTMENTS = debugOnly {
        listOf(
            listOf("Alphabet (Google)", "20", "323.44", "Stocks", "USD", "6048000"),
            listOf("Microsoft Corp.", "15", "485.5", "Stocks", "USD", "4752000"),
            listOf("Amazon.com Inc.", "17", "229.16", "Stocks", "USD", "4752000"),
            listOf("Tesla Inc.", "19", "419.40", "Stocks", "USD", "3024000"),
            listOf("NVIDIA Corp.", "16", "180.26", "Stocks", "USD", "3024000"),
            listOf("Apple Inc.", "20", "276.97", "Stocks", "USD", "2160000"),
            listOf("Meta Platforms", "15", "636.22", "Stocks", "USD", "2160000"),
            listOf("Vanguard S&P 500 ETF", "32", "621.88", "ETF", "USD", "1296000"),
            listOf("Bitcoin", "0.025", "90966", "Cryptocurrency", "USD", "1296000"),
            listOf("Ethereum", "0.08", "3021", "Cryptocurrency", "USD", "864000"),
            listOf("US Government Bonds", "2", "2000.00", "Bonds", "USD", "864000"),
            listOf("Rental Property Deposit", "1", "17000.00", "Real Estate", "USD", "432000"),
            listOf("Alibaba Group", "30", "133", "Stocks", "EUR", "432000"),
            listOf("Samsung Electronics", "2.4", "1128", "Stocks", "EUR", "0"),
            listOf("Toyota Motor Corp.", "15", "210", "Stocks", "EUR", "0"),
        )
    }.mapIndexed { index, (name, shares, cost, category, currency, dateOffset) ->
        Investment(
            id = index.toString(),
            name = name,
            shares = shares.toBigDecimal(),
            cost = cost.toBigDecimal(),
            category = category,
            currency = currency,
            date = System.currentTimeMillis() - (dateOffset.toLong() * 1_000),
            isDeleted = false,
            createdAt = System.currentTimeMillis() - (dateOffset.toLong() * 1_000),
        )
    }

    val SAMPLE_INVESTMENTS_GROUPED = Group.byDate(SAMPLE_INVESTMENTS, Investment::date, Investment::total)

    val SAMPLE_INVESTMENTS_NET_WORTH = Group.groupInvestmentNetWorth(SAMPLE_INVESTMENTS)

    val SAMPLE_INVESTMENTS_CATEGORIES = Group.groupInvestmentCategories(SAMPLE_INVESTMENTS)

    val SAMPLE_INVESTMENTS_ASSETS = Group.groupInvestmentAssets(SAMPLE_INVESTMENTS)

    val SAMPLE_WEIGHTS = debugOnly {
        listOf(
            listOf("70.5", "1700000000"),
            listOf("70.7", "1700086400"),
            listOf("70.6", "1700172800"),
            listOf("70.8", "1700259200"),
            listOf("70.9", "1700345600"),
            listOf("71.0", "1700432000"),
            listOf("70.8", "1700518400"),
            listOf("70.7", "1700604800"),
            listOf("70.6", "1700691200"),
            listOf("70.5", "1700777600"),
        )
    }.mapIndexed { index, (weight, timestamp) ->
        Weight(
            id = index.toString(),
            value = weight.toBigDecimal(),
            date = timestamp.toLong() * 1000,
            isDeleted = false,
            createdAt = timestamp.toLong() * 1000,
        )
    }

    private fun debugOnly(list: () -> List<List<String>>): List<List<String>> {
        if (BuildConfig.DEBUG) {
            return list()
        }

        return listOf()
    }

    private operator fun <T> List<T>.component6(): T = this[5]
    private operator fun <T> List<T>.component7(): T = this[6]

}