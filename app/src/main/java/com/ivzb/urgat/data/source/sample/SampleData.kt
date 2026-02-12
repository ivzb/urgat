package com.ivzb.urgat.data.source.sample

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.common.Group
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.randomUUID
import kotlin.collections.component1
import kotlin.collections.component4

object SampleData {

    val SAMPLE_TODOS =
        listOf(
            listOf("Morning workout\n30 minutes of cardio and stretching", "true"),
            listOf("Buy groceries\nMilk, eggs, bread, and fruits", "false"),
            listOf("Read a book\nFinish reading 'Atomic Habits'", "false"),
            listOf("Call plumber\nFix leaking sink in the kitchen", "false"),
            listOf("Plan weekend trip\nDecide on destination and book hotel", "false"),
            listOf("Send work report\nMonthly sales report to manager", "true"),
            listOf("Clean kitchen\nWash dishes, wipe counters, sweep floor", "false"),
            listOf("Update resume\nAdd latest projects and experiences", "false"),
            listOf("Meditation\n10 minutes of morning meditation", "true"),
            listOf("Water plants\nWater all indoor and outdoor plants", "false"),
            listOf("Prepare presentation\nSlides for Monday's team meeting", "false"),
            listOf("Pay electricity bill\nDue by the end of the month", "true"),
            listOf("Organize closet\nSort clothes and donate unused items", "false"),
            listOf("Write journal\nReflect on today's achievements and challenges", "false"),
            listOf("Grocery meal prep\nCook meals for the next 3 days", "true")
        )
            .mapIndexed { index, (content, isCompleted) ->
                Todo(
                    id = "todo_$index",
                    content = content,
                    isCompleted = isCompleted.toBoolean(),
                    isDeleted = false,
                    createdAt = System.currentTimeMillis(),
                )
            }

    val SAMPLE_EXPENSES =
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
            listOf("Salary", "-100", "Job", "432000"),
            listOf("Pet food", "25.50", "Pets", "0"),
            listOf("Birthday gift", "80", "Gifts", "0"),
            listOf("Credit card", "50", "Debt", "0"),
        )
            .reversed().mapIndexed { index, (name, amount, category, dateOffset) ->
                Expense(
                    id = "expense_$index",
                    name = name,
                    amount = amount.toBigDecimal(),
                    category = category,
                    date = System.currentTimeMillis() - (dateOffset.toLong() * 1_000),
                    excludeFromAnalytics = false,
                    isDeleted = false,
                    createdAt = System.currentTimeMillis() - (dateOffset.toLong() * 1_000),
                )
            }

    val SAMPLE_EXPENSES_GROUPED =
        Group.byDateMonthly(SAMPLE_EXPENSES, Expense::date, Expense::total)

    val SAMPLE_EXPENSES_CATEGORIES_MONTHLY =
        Group.groupExpenseCategoriesMonthly(SAMPLE_EXPENSES, ::randomUUID)

    val SAMPLE_EXPENSES_CATEGORIES_YEARLY =
        Group.groupExpenseCategoriesYearly(SAMPLE_EXPENSES, ::randomUUID)

    val SAMPLE_EXPENSES_CATEGORIES_ALL_TIME =
        Group.groupExpenseCategoriesAllTime(SAMPLE_EXPENSES, ::randomUUID)

    val SAMPLE_NOTES = listOf(
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
        .mapIndexed { index, (content) ->
            Note(
                id = "note_$index",
                content = content,
                isDeleted = false,
                createdAt = System.currentTimeMillis(),
            )
        }

    val SAMPLE_INVESTMENTS = listOf(
        listOf("Alphabet (Google)", "0.20", "323.44", "Stocks", "USD", "6048000"),
        listOf("Microsoft Corp.", "0.15", "485.5", "Stocks", "USD", "4752000"),
        listOf("Amazon.com Inc.", "0.17", "229.16", "Stocks", "USD", "4752000"),
        listOf("Tesla Inc.", "0.19", "419.40", "Stocks", "USD", "3024000"),
        listOf("NVIDIA Corp.", "0.16", "180.26", "Stocks", "USD", "3024000"),
        listOf("Apple Inc.", "0.20", "276.97", "Stocks", "USD", "2160000"),
        listOf("Meta Platforms", "0.15", "636.22", "Stocks", "USD", "2160000"),
        listOf("Vanguard S&P 500 ETF", "0.32", "621.88", "ETF", "USD", "1296000"),
        listOf("Bitcoin", "0.025", "90966", "Cryptocurrency", "USD", "1296000"),
        listOf("Ethereum", "0.08", "3021", "Cryptocurrency", "USD", "864000"),
        listOf("US Government Bonds", "0.2", "2000.00", "US Bonds", "USD", "864000"),
        listOf("Rental Property Deposit", "0.1", "17000.00", "Real Estate", "USD", "432000"),
        listOf("Alibaba Group", "0.30", "133", "Stocks", "EUR", "432000"),
        listOf("Samsung Electronics", "0.24", "1128", "Stocks", "EUR", "0"),
        listOf("Toyota Motor Corp.", "0.15", "210", "Stocks", "EUR", "0"),
    )
        .reversed().mapIndexed { index, (name, shares, cost, category, currency, dateOffset) ->
            Investment(
                id = "investment_$index",
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

    val SAMPLE_INVESTMENTS_GROUPED =
        Group.byDateMonthly(SAMPLE_INVESTMENTS, Investment::date, Investment::total)

    val SAMPLE_INVESTMENTS_CATEGORIES_YEARLY =
        Group.groupInvestmentCategoriesYearly(SAMPLE_INVESTMENTS, ::randomUUID)

    val SAMPLE_INVESTMENTS_NET_WORTH = Group.groupInvestmentNetWorth(SAMPLE_INVESTMENTS)

    val SAMPLE_INVESTMENTS_CATEGORIES =
        Group.groupInvestmentCategories(SAMPLE_INVESTMENTS, ::randomUUID)

    val SAMPLE_INVESTMENTS_ASSETS = Group.groupInvestmentAssets(SAMPLE_INVESTMENTS, ::randomUUID)

    val SAMPLE_SAVINGS =
        listOf(
            listOf("Emergency fund", "500"),
            listOf("Kids fund", "200"),
            listOf("Cash", "250"),

            listOf("Emergency fund", "500"),
            listOf("Kids fund", "200"),
            listOf("Cash", "250"),

            listOf("Emergency fund", "500"),
            listOf("Kids fund", "200"),
            listOf("Cash", "250"),

            listOf("Emergency fund", "500"),
            listOf("Kids fund", "200"),
            listOf("Cash", "250"),

            listOf("Emergency fund", "500"),
            listOf("Kids fund", "200"),
            listOf("Cash", "250"),
        )
            .mapIndexed { index, (name, amount) ->
                Saving(
                    id = "saving_$index",
                    name = name,
                    amount = amount.toBigDecimal(),
                    date = System.currentTimeMillis() - ((index / 3) * 86_400 * 31 * 1_000L),
                    isDeleted = false,
                    createdAt = System.currentTimeMillis() - ((index / 3) * 86_400 * 31 * 1_000L),
                )
            }

    val SAMPLE_SAVINGS_GROUPED =
        Group.byDateMonthly(SAMPLE_SAVINGS, Saving::date, Saving::total)

    val SAMPLE_SAVINGS_CATEGORIES_MONTHLY =
        Group.groupSavingCategoriesMonthly(SAMPLE_SAVINGS, ::randomUUID)

    val SAMPLE_SAVINGS_CATEGORIES_YEARLY =
        Group.groupSavingCategoriesYearly(SAMPLE_SAVINGS, ::randomUUID)

    val SAMPLE_SAVINGS_CATEGORIES_ALL_TIME =
        Group.groupSavingCategoriesAllTime(SAMPLE_SAVINGS, ::randomUUID)

    val SAMPLE_WEIGHTS = listOf(
        listOf("70.5"),
        listOf("70.7"),
        listOf("70.6"),
        listOf("70.8"),
        listOf("70.9"),
        listOf("71.0"),
        listOf("70.8"),
        listOf("70.7"),
        listOf("70.6"),
        listOf("70.5"),
    )
        .mapIndexed { index, (weight) ->
            Weight(
                id = "weight_$index",
                value = weight.toBigDecimal(),
                date = System.currentTimeMillis() - (index * 86_400 * 1_000),
                isDeleted = false,
                createdAt = System.currentTimeMillis() - (index * 86_400 * 1_000),
            )
        }

    val SAMPLE_BUDGETS = listOf(
        listOf("3000", "5000", "6200",),
        listOf("3200", "6200", "7000",),
        listOf("3100", "7000", "7600",),
        listOf("3300", "7600", "8400",),
        listOf("3400", "8400", "9100",),
        listOf("3500", "9100", "9800",),
        listOf("3600", "9800", "10500",),
        listOf("3700", "10500", "11300",),
        listOf("3800", "11300", "12000",),
        listOf("3900", "12000", "12300",),
    )
        .reversed()
        .mapIndexed { index, (income, bankStart, bankEnd) ->
            Budget(
                id = "budget_$index",
                income = income.toBigDecimal(),
                bankStart = bankStart.toBigDecimal(),
                bankEnd = bankEnd.toBigDecimal(),
                date = System.currentTimeMillis() - (index * 86_400 * 31 * 1_000L),
                isDeleted = false,
                createdAt = System.currentTimeMillis() - (index * 86_400 * 31 * 1_000L),
            )
        }

    val SAMPLE_DASHBOARD_BUDGET = GroupedBudget(
        category = Group.groupBudgetCategoriesMonthly(SAMPLE_BUDGETS, ::randomUUID).first(),
        totalIncome = 4021.34.toBigDecimal(),
        totalExpenses = 571.34.toBigDecimal(),
        totalInvestments = 2500.toBigDecimal(),
        totalInvestmentsFormatted = "2500 USD",
        totalSavings = 950.toBigDecimal(),
    )

    private operator fun <T> List<T>.component6(): T = this[5]
    private operator fun <T> List<T>.component7(): T = this[6]

}