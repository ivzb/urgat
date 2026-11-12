package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.ExpenseAverage
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.domain.common.Group.YEAR_MONTH_FORMATTER
import com.ivzb.urgat.domain.common.Group.byDateMonthly
import com.ivzb.urgat.domain.common.Group.byDateYearly
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.ui.components.GroupedItems
import com.ivzb.urgat.util.toLocalDate
import java.time.YearMonth
import kotlin.collections.component1
import kotlin.collections.component2

object GroupExpenses {

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

}
