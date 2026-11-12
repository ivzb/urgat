package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.data.budget.BudgetCategory
import com.ivzb.urgat.domain.budget.GroupedBudgetCategories
import com.ivzb.urgat.domain.common.Group.byDateMonthly
import com.ivzb.urgat.domain.common.Group.byDateYearly
import com.ivzb.urgat.ui.components.GroupedItems
import kotlin.collections.component1
import kotlin.collections.component2

object GroupBudgets {

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

}
