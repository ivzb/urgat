package com.ivzb.urgat.data.source.sample

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.data.carMaintenance.CarMaintenance
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.watchlist.WatchlistAsset
import com.ivzb.urgat.data.weight.Weight
import com.ivzb.urgat.domain.common.Group
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
                    currency = "EUR",
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
        listOf("GOOGL", "15", "323.44", "Stocks", "USD", "6048000"),
        listOf("MSFT", "10", "485.5", "Stocks", "USD", "4752000"),
        listOf("AMZN", "20", "229.16", "Stocks", "USD", "4752000"),
        listOf("BRK.B", "10", "450", "ETF", "USD", "4552000"),
        listOf("TSLA", "12", "419.40", "Stocks", "USD", "3024000"),
        listOf("NVDA", "25", "180.26", "Stocks", "USD", "3024000"),
        listOf("AAPL", "18", "276.97", "Stocks", "USD", "2160000"),
        listOf("META", "8", "636.22", "Stocks", "USD", "2160000"),
        listOf("SXR8", "10", "550", "ETF", "EUR", "1760000"),
        listOf("VOO", "30", "621.88", "ETF", "USD", "1296000"),
        listOf("BTC", "0.5", "90966", "Cryptocurrency", "USD", "1296000"),
        listOf("FLUT", "30", "195", "Stocks", "EUR", "1296000"),
        listOf("ETH", "3", "3021", "Cryptocurrency", "USD", "864000"),
        listOf("TLT", "50", "88.50", "US Bonds", "USD", "864000"),
        listOf("XAU", "10", "2500", "Commodity", "EUR", "664000"),
        listOf("VNQ", "25", "82.40", "Real Estate", "USD", "432000"),
        listOf("BABA", "40", "133", "Stocks", "EUR", "432000"),
        listOf("SSNLF", "5", "1128", "Stocks", "EUR", "0"),
        listOf("TM", "30", "210", "Stocks", "EUR", "0"),
    )
        .reversed().mapIndexed { index, (name, shares, cost, category, currency, dateOffset) ->
            Investment(
                id = "investment_$index",
                name = name,
                description = null,
                shares = shares.toBigDecimal(),
                costPrice = cost.toBigDecimal(),
                marketPrice = cost.toBigDecimal() + (-100 .. 100).random().toBigDecimal(),
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

    val SAMPLE_INVESTMENTS_ASSETS =
        Group.groupInvestmentAssets(SAMPLE_INVESTMENTS, ::randomUUID)

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
                    currency = "EUR",
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

    val SAMPLE_WATCHLIST_ASSETS = listOf(
        listOf("GOOGL", "323.44", "USD"),
        listOf("MSFT", "485.5", "USD"),
        listOf("AMZN", "229.16", "USD"),
        listOf("TSLA", "419.40", "USD"),
        listOf("NVDA", "180.26", "USD"),
        listOf("AAPL", "276.97", "USD"),
        listOf("META", "636.22", "USD"),
        listOf("BTC", "90966", "USD"),
        listOf("ETH", "3021", "USD"),
        listOf("VOO", "580.03", "USD"),
    )
        .mapIndexed { index, (name, cost, currency) ->
            val marketPrice = cost.toBigDecimal() + (-100 .. 100).random().toBigDecimal()
            val previousClosePrice = marketPrice + (-5 .. 5).random().toBigDecimal()

            WatchlistAsset(
                id = "watchlist_asset_$index",
                name = name,
                description = null,
                marketPrice = marketPrice,
                previousClosePrice = previousClosePrice,
                pricePrecision = null,
                currency = currency,
                isDeleted = false,
                createdAt = System.currentTimeMillis(),
            )
        }

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
                currency = "EUR",
                date = System.currentTimeMillis() - (index * 86_400 * 31 * 1_000L),
                isDeleted = false,
                createdAt = System.currentTimeMillis() - (index * 86_400 * 31 * 1_000L),
            )
        }

    val SAMPLE_REPORT_BUDGET = GroupedBudget(
        category = Group.groupBudgetCategoriesMonthly(SAMPLE_BUDGETS, ::randomUUID).first(),
        currency = "EUR",
        totalIncome = 4_021.34.toBigDecimal(),
        totalExpenses = 571.34.toBigDecimal(),
        totalInvestments = 2_500.toBigDecimal(),
        totalSavings = 950.toBigDecimal(),
    )

    val SAMPLE_FUEL_CONSUMPTION = listOf(
        listOf("45.5", "106.93", "EUR", "Petrol", "Mercedes w140", "150000", "125", "0"),
        listOf("42.0", "77.70", "EUR", "Diesel", "BMW e39", "220000", "158", "3"),
        listOf("40.0", "96.00", "EUR", "Petrol", "Mercedes w140", "149875", "657", "7"),
        listOf("38.5", "73.15", "EUR", "Diesel", "BMW e39", "219842", "675", "10"),
        listOf("50.0", "115.00", "EUR", "Petrol", "Mercedes w140", "149218", "575", "14"),
        listOf("44.0", "80.08", "EUR", "Diesel", "BMW e39", "219167", "606", "17"),
        listOf("42.0", "99.96", "EUR", "Petrol", "Mercedes w140", "148643", "657", "21"),
        listOf("40.5", "76.14", "EUR", "Diesel", "BMW e39", "218561", "627", "24"),
        listOf("48.0", "111.36", "EUR", "Petrol", "Mercedes w140", "147986", "632", "28"),
        listOf("46.0", "82.80", "EUR", "Diesel", "BMW e39", "217934", "506", "31"),
        listOf("38.5", "93.17", "EUR", "Petrol", "Mercedes w140", "147354", "475", "35"),
        listOf("37.0", "71.04", "EUR", "Diesel", "BMW e39", "217428", "647", "38"),
        listOf("46.0", "108.56", "EUR", "Petrol", "Mercedes w140", "146879", "686", "42"),
        listOf("43.0", "79.98", "EUR", "Diesel", "BMW e39", "216781", "635", "45"),
        listOf("41.0", "97.99", "EUR", "Petrol", "Mercedes w140", "146193", "572", "49"),
        listOf("39.5", "74.66", "EUR", "Diesel", "BMW e39", "216146", "612", "52"),
        listOf("49.0", "113.19", "EUR", "Petrol", "Mercedes w140", "145621", "673", "56"),
        listOf("47.0", "85.07", "EUR", "Diesel", "BMW e39", "215534", "617", "59"),
        listOf("43.5", "103.10", "EUR", "Petrol", "Mercedes w140", "144948", "", "63"),
        listOf("41.5", "77.61", "EUR", "Diesel", "BMW e39", "214917", "", "66"),
    )
        .mapIndexed { index, (amount, price, currency, type, carName, odometer, distance, dateOffset) ->
            FuelConsumption(
                id = "fuel_consumption_$index",
                amount = amount.toBigDecimal(),
                price = price.toBigDecimal(),
                currency = currency,
                type = type,
                carName = carName,
                odometer = odometer.toInt(),
                distance = distance.toIntOrNull(),
                date = System.currentTimeMillis() - (dateOffset.toLong() * 86_400 * 1_000L),
                isDeleted = false,
                createdAt = System.currentTimeMillis() - (dateOffset.toLong() * 86_400 * 1_000L),
            )
        }

    val SAMPLE_FUEL_CONSUMPTION_GROUPED =
        Group.byDateMonthly(SAMPLE_FUEL_CONSUMPTION, FuelConsumption::date, FuelConsumption::total)

    val SAMPLE_FUEL_CONSUMPTION_CATEGORIES_MONTHLY =
        Group.groupFuelConsumptionCategoriesMonthly(SAMPLE_FUEL_CONSUMPTION, ::randomUUID)

    val SAMPLE_FUEL_CONSUMPTION_CATEGORIES_YEARLY =
        Group.groupFuelConsumptionCategoriesYearly(SAMPLE_FUEL_CONSUMPTION, ::randomUUID)

    val SAMPLE_FUEL_CONSUMPTION_CATEGORIES_ALL_TIME =
        Group.groupFuelConsumptionCategoriesAllTime(SAMPLE_FUEL_CONSUMPTION, ::randomUUID)

    val SAMPLE_CAR_MAINTENANCE = listOf(
        listOf("Oil change", "5W-30 Full Synthetic", "https://example.com/oil", "Mercedes w140", "150000", "0"),
        listOf("Brake pads replacement", "", "", "BMW e39", "220000", "7"),
        listOf("Air filter", "Mann C 30 153", "", "Mercedes w140", "149500", "14"),
        listOf("Spark plugs", "NGK BKR6E", "https://example.com/spark", "BMW e39", "219500", "21"),
        listOf("Coolant flush", "G12+ Antifreeze", "", "Mercedes w140", "149000", "28"),
        listOf("Timing belt", "", "https://example.com/belt", "BMW e39", "219000", "35"),
        listOf("Transmission fluid", "ATF Dexron VI", "", "Mercedes w140", "148500", "42"),
        listOf("Battery replacement", "Varta E11 74Ah", "", "BMW e39", "218500", "49"),
        listOf("Cabin filter", "Bosch 1987432071", "", "Mercedes w140", "148000", "56"),
        listOf("Wheel alignment", "", "", "BMW e39", "218000", "63"),
    )
        .mapIndexed { index, (description, partNumber, url, carName, odometer, dateOffset) ->
            CarMaintenance(
                id = "car_maintenance_$index",
                description = description,
                partNumber = partNumber.ifEmpty { null },
                url = url.ifEmpty { null },
                carName = carName,
                odometer = odometer.toInt(),
                date = System.currentTimeMillis() - (dateOffset.toLong() * 86_400 * 1_000L),
                isDeleted = false,
                createdAt = System.currentTimeMillis() - (dateOffset.toLong() * 86_400 * 1_000L),
            )
        }

    val SAMPLE_CAR_MAINTENANCE_GROUPED =
        Group.byDateMonthly(SAMPLE_CAR_MAINTENANCE, CarMaintenance::date) { it.size }

    val SAMPLE_FIRE = FiRe(
        selection = "Feb 2025",
        currency = "EUR",
        monthlyIncome = 5_000.toBigDecimal(),
        monthlyExpenses = 2_500.toBigDecimal(),
        monthlyInvestments = 1_500.toBigDecimal(),
        annualIncome = 60_000.toBigDecimal(),
        annualExpenses = 30_000.toBigDecimal(),
        annualInvestments = 18_000.toBigDecimal(),
        accumulatedStash = 150_000.toBigDecimal(),
        savingsRate = 50.toBigDecimal(),
        target = 750_000.toBigDecimal(),
        withdrawalRate = 4.toBigDecimal(),
        returnOfInvestment = 7.toBigDecimal(),
        taxRate = 10.toBigDecimal(),
        yearsUntilFiRe = 12.5.toBigDecimal(),
    )

    val SAMPLE_TRADES = listOf(
        listOf("BTC", "27500", "USD", "180"),
        listOf("BTC", "31000", "USD", "150"),
        listOf("BTC", "29000", "USD", "120"),
        listOf("BTC", "35000", "USD", "90"),
        listOf("BTC", "42000", "USD", "75"),
        listOf("BTC", "38000", "USD", "60"),
        listOf("BTC", "45000", "USD", "45"),
        listOf("BTC", "52000", "USD", "30"),
        listOf("BTC", "60000", "USD", "14"),
        listOf("BTC", "57000", "USD", "7"),
        listOf("BTC", "63000", "USD", "3"),
        listOf("BTC", "61000", "USD", "0"),
    )
        .mapIndexed { index, (name, amount, currency, dateOffset) ->
            Trade(
                id = "trade_$index",
                name = name,
                amount = amount.toBigDecimal(),
                currency = currency,
                date = System.currentTimeMillis() - (dateOffset.toLong() * 86_400 * 1_000L),
                isDeleted = false,
                createdAt = System.currentTimeMillis() - (dateOffset.toLong() * 86_400 * 1_000L),
            )
        }

    val SAMPLE_TRADES_GROUPED =
        Group.byDateMonthly(SAMPLE_TRADES, Trade::date, Trade::total)

    private operator fun <T> List<T>.component6(): T = this[5]
    private operator fun <T> List<T>.component7(): T = this[6]
    private operator fun <T> List<T>.component8(): T = this[7]

}