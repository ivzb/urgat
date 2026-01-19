package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.data.expense.Expense
import com.ivzb.urgat.data.expense.Expense.Companion.format
import com.ivzb.urgat.data.expense.ExpenseCategory
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.expense.GroupedExpenseCategories
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
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

    fun groupInvestmentCategoriesMonthly(investments: List<Investment>, random: () -> String): List<GroupedInvestmentCategories> =
        byDateMonthly(investments, Investment::date, Investment::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupInvestments(items, random),
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

    fun groupInvestmentAssets(investments: List<Investment>, random: () -> String): List<InvestmentAsset> =
        investments
            .groupBy { Triple(it.name, it.category, it.currency) }
            .map { (key, investments) ->
                val (name, category, currency) = key

                InvestmentAsset(
                    id = random(),
                    name = name,
                    category = category,
                    shares = investments.sumOf { it.shares },
                    marketValue = investments.sumOf { it.marketValue },
                    currency = currency,
                    total = Investment.total(investments),

                    investments = investments,
                )
            }
            .sortedBy { it.name }

    fun groupInvestmentCategories(investments: List<Investment>, random: () -> String): List<InvestmentCategory> =
        groupInvestments(investments, random)

    fun groupInvestmentNetWorth(investments: List<Investment>): InvestmentNetWorth =
        InvestmentNetWorth(
            total = Investment.total(investments),
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

    private fun groupExpenses(expenses: List<Expense>, random: () -> String): List<ExpenseCategory> =
        expenses
            .filterNot { it.excludeFromAnalytics }
            .groupBy { it.category }
            .map { (category, expenses) ->
                ExpenseCategory(
                    id = random(),
                    name = category,
                    value = expenses.format(),

                    expenses = expenses,
                )
            }
            .sortedByDescending { it.value }

    private fun groupInvestments(investments: List<Investment>, random: () -> String): List<InvestmentCategory> =
        investments
            .groupBy { it.category }
            .map { (category, investments) ->
                InvestmentCategory(
                    id = random(),
                    name = category,
                    total = Investment.total(investments),

                    investments = investments,
                )
            }
            .sortedBy { it.name }

    fun groupBudgetCategoriesMonthly(budgets: List<Budget>, random: () -> String): List<GroupedBudgetCategories> =
        byDateMonthly(budgets, Budget::date, Budget::total)
            .map { (group, total, items) ->
                GroupedItems(
                    group = group,
                    total = total,
                    items = groupBudgets(items, random),
                )
            }

    private fun groupBudgets(budgets: List<Budget>, random: () -> String): List<BudgetCategory> =
        budgets
            .groupBy { it.id }
            .map { (id, budgets) ->
                BudgetCategory(
                    id = random(),
                    name = id,
                    value = budgets.sumOf { it.income },
                )
            }
            .sortedByDescending { it.value }

}