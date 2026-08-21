package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.Expense.Companion.format
import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.expense.ExpenseTotal.Companion.sumByCurrency
import com.ivzb.urgat.data.fuelConsumption.FuelConsumption
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionCategory
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.data.trade.Trade
import com.ivzb.urgat.data.trade.TradeCategory
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.fuelConsumption.GroupedFuelConsumptionCategories
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.domain.saving.GroupedSavingCategories
import com.ivzb.urgat.domain.trades.GroupedTradeCategories
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.toLocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2

object Group {

    private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy")

    fun <Item, Total> byDateMonthly(
        items: List<Item>,
        date: (Item) -> Long,
        total: (List<Item>) -> Total,
    ): List<GroupedItems<String, Total, Item>> =
        items
            .groupBy {
                val date = date(it).toLocalDate()
                YearMonth.from(date)
            }
            .map { (yearMonth, items) ->
                GroupedItems(
                    group = yearMonth,
                    total = total(items),
                    items = items,
                )
            }
            .sortedByDescending { it.group }
            .map {
                GroupedItems(
                    group = it.group.format(YEAR_MONTH_FORMATTER),
                    total = it.total,
                    items = it.items,
                )
            }

    fun <Item, Total> byDateYearly(
        items: List<Item>,
        date: (Item) -> Long,
        total: (List<Item>) -> Total
    ): List<GroupedItems<String, Total, Item>> =
        items
            .groupBy {
                val date = date(it).toLocalDate()
                Year.from(date)
            }
            .map { (year, items) ->
                GroupedItems(
                    group = year,
                    total = total(items),
                    items = items,
                )
            }
            .sortedByDescending { it.group }
            .map {
                GroupedItems(
                    group = it.group.format(YEAR_FORMATTER),
                    total = it.total,
                    items = it.items
                )
            }

    fun <Item, Total> byDateAllTime(
        items: List<Item>,
        total: (List<Item>) -> Total
    ): GroupedItems<String, Total, Item> =
        items
            .let {
                GroupedItems(
                    group = "",
                    total = total(it),
                    items = it,
                )
            }

    fun groupInvestmentCategoriesYearly(investments: List<Investment>, random: () -> String): List<GroupedInvestmentCategories> =
        byDateYearly(investments, Investment::date, Investment::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupInvestments(items, random),
                )
            }

    fun groupInvestmentAssets(
        investments: List<Investment>,
        random: () -> String,
    ): List<InvestmentAsset> =
        investments
            .groupBy { Triple(it.name, it.category, it.currency) }
            .map { (key, investments) ->
                val (name, category, currency) = key

                InvestmentAsset(
                    id = random(),
                    name = name,
                    category = category,
                    shares = investments.sumOf { it.shares },
                    costPrice = investments.sumOf { it.totalCostPrice },
                    marketPrice = investments.sumOf { it.totalMarketPrice },
                    currency = currency,
                    totals = Investment.total(investments),

                    investments = investments,
                )
            }
            .sortedBy { it.name }

    fun groupInvestmentCategories(investments: List<Investment>, random: () -> String): List<InvestmentCategory> =
        groupInvestments(investments, random)

    fun groupInvestmentNetWorth(investments: List<Investment>): InvestmentNetWorth =
        InvestmentNetWorth(
            totals = Investment.total(investments),
        )

    fun groupExpenseCategoriesMonthly(expenses: List<Expense>, random: () -> String): List<GroupedExpenseCategories> =
        byDateMonthly(expenses, Expense::date, Expense::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupExpenses(items, random),
                )
            }

    fun groupExpenseCategoriesYearly(expenses: List<Expense>, random: () -> String): List<GroupedExpenseCategories> =
        byDateYearly(expenses, Expense::date, Expense::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupExpenses(items, random),
                )
            }

    fun groupExpenseCategoriesAllTime(expenses: List<Expense>, random: () -> String): List<ExpenseCategory> =
        groupExpenses(expenses, random)

    /**
     * Average monthly expenses of the year each month belongs to, keyed by the monthly group.
     */
    fun groupExpenseAveragesMonthly(expenses: List<Expense>): Map<String, ExpenseAverage> =
        expenses
            .groupBy { it.date.toLocalDate().year }
            .flatMap { (_, yearlyExpenses) ->
                val average = expenseAverage(yearlyExpenses)

                yearlyExpenses
                    .map { YearMonth.from(it.date.toLocalDate()).format(YEAR_MONTH_FORMATTER) }
                    .distinct()
                    .map { month -> month to average }
            }
            .toMap()

    /**
     * Average monthly expenses of each year, keyed by the yearly group.
     */
    fun groupExpenseAveragesYearly(expenses: List<Expense>): Map<String, ExpenseAverage> =
        byDateYearly(expenses, Expense::date, ::expenseAverage)
            .associate { (group, average, _) -> group to average }

    fun groupExpenseAverageAllTime(expenses: List<Expense>): ExpenseAverage =
        expenseAverage(expenses)

    /**
     * Averages are spread over the months of the whole period,
     * so every category is comparable within the same period.
     *
     * A single month has no meaningful average, as it is the same as its total.
     */
    private fun expenseAverage(expenses: List<Expense>): ExpenseAverage {
        val months = Expense.months(expenses)

        if (months <= 1) {
            return ExpenseAverage.EMPTY
        }

        return ExpenseAverage(
            totals = Expense.averageMonthly(expenses, months),
            categories = expenses
                .groupBy { it.category }
                .mapValues { (_, categoryExpenses) -> Expense.averageMonthly(categoryExpenses, months) },
        )
    }

    private fun groupExpenses(expenses: List<Expense>, random: () -> String): List<ExpenseCategory> =
        expenses
            .filterNot { it.excludeFromAnalytics }
            .groupBy { it.category }
            .map { (category, expenses) ->
                ExpenseCategory(
                    id = random(),
                    name = category,
                    totals = Expense.total(expenses),

                    expenses = expenses,
                )
            }
            .sortedByDescending { it.totals.sumOf { it.amount } }

    private fun groupInvestments(investments: List<Investment>, random: () -> String): List<InvestmentCategory> =
        investments
            .groupBy { it.category }
            .map { (category, investments) ->
                InvestmentCategory(
                    id = random(),
                    name = category,
                    totals = Investment.total(investments),

                    investments = investments,
                )
            }
            .sortedBy { it.name }

    fun groupSavingCategoriesMonthly(savings: List<Saving>, random: () -> String): List<GroupedSavingCategories> =
        byDateMonthly(savings, Saving::date, Saving::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupSavings(items, random),
                )
            }

    fun groupSavingCategoriesYearly(savings: List<Saving>, random: () -> String): List<GroupedSavingCategories> =
        byDateYearly(savings, Saving::date, Saving::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupSavings(items, random),
                )
            }

    fun groupSavingCategoriesAllTime(savings: List<Saving>, random: () -> String): List<SavingCategory> =
        groupSavings(savings, random)

    private fun groupSavings(savings: List<Saving>, random: () -> String): List<SavingCategory> =
        savings
            .groupBy { it.name }
            .map { (category, savings) ->
                SavingCategory(
                    id = random(),
                    name = category,
                    totals = Saving.total(savings),

                    savings = savings,
                )
            }
            .sortedByDescending { it.totals.sumOf { it.amount } }

    fun groupTradeCategoriesMonthly(trades: List<Trade>, random: () -> String): List<GroupedTradeCategories> =
        byDateMonthly(trades, Trade::date, Trade::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupTrades(items, random),
                )
            }

    fun groupTradeCategoriesYearly(trades: List<Trade>, random: () -> String): List<GroupedTradeCategories> =
        byDateYearly(trades, Trade::date, Trade::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupTrades(items, random),
                )
            }

    fun groupTradeCategoriesAllTime(trades: List<Trade>, random: () -> String): List<TradeCategory> =
        groupTrades(trades, random)

    private fun groupTrades(trades: List<Trade>, random: () -> String): List<TradeCategory> =
        trades
            .groupBy { it.name }
            .map { (category, trades) ->
                TradeCategory(
                    id = random(),
                    name = category,
                    totals = Trade.total(trades),

                    trades = trades,
                )
            }
            .sortedByDescending { it.totals.sumOf { it.price } }

    fun groupBudgetCategoriesMonthly(budgets: List<Budget>, random: () -> String): List<GroupedBudgetCategories> =
        byDateMonthly(budgets, Budget::date, Budget::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupBudgets(items, random),
                )
            }

    fun groupBudgetCategoriesYearly(budgets: List<Budget>, random: () -> String): List<GroupedBudgetCategories> =
        byDateYearly(budgets, Budget::date, Budget::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupBudgets(items, random),
                )
            }

    fun groupBudgetCategoriesAllTime(budgets: List<Budget>, random: () -> String): BudgetCategory =
        BudgetCategory(
            id = random(),
            name = random(),
            currency = budgets.first().currency,
            value = budgets.sumOf { it.income },
        )

    private fun groupBudgets(budgets: List<Budget>, random: () -> String): List<BudgetCategory> =
        budgets
            .groupBy { it.id }
            .map { (id, budgets) ->
                BudgetCategory(
                    id = random(),
                    name = id,
                    currency = budgets.first().currency,
                    value = budgets.sumOf { it.income },
                )
            }
            .sortedByDescending { it.value }

    fun groupFuelConsumptionCategoriesMonthly(fuelConsumption: List<FuelConsumption>, random: () -> String): List<GroupedFuelConsumptionCategories> =
        byDateMonthly(fuelConsumption, FuelConsumption::date, FuelConsumption::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupFuelConsumption(items, random),
                )
            }

    fun groupFuelConsumptionCategoriesYearly(fuelConsumption: List<FuelConsumption>, random: () -> String): List<GroupedFuelConsumptionCategories> =
        byDateYearly(fuelConsumption, FuelConsumption::date, FuelConsumption::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupFuelConsumption(items, random),
                )
            }

    fun groupFuelConsumptionCategoriesAllTime(fuelConsumption: List<FuelConsumption>, random: () -> String): List<FuelConsumptionCategory> =
        groupFuelConsumption(fuelConsumption, random)

    private fun groupFuelConsumption(fuelConsumption: List<FuelConsumption>, random: () -> String): List<FuelConsumptionCategory> =
        fuelConsumption
            .groupBy { it.type }
            .map { (category, fuelConsumption) ->
                FuelConsumptionCategory(
                    id = random(),
                    name = category,
                    totals = FuelConsumption.total(fuelConsumption),

                    fuelConsumption = fuelConsumption,
                )
            }
            .sortedBy { it.name }

}