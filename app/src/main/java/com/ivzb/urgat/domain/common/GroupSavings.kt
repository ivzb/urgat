package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.saving.Saving
import com.ivzb.urgat.data.saving.SavingCategory
import com.ivzb.urgat.domain.common.Group.byDateMonthly
import com.ivzb.urgat.domain.common.Group.byDateYearly
import com.ivzb.urgat.domain.saving.GroupedSavingCategories
import com.ivzb.urgat.ui.components.GroupedItems
import kotlin.collections.component1
import kotlin.collections.component2

object GroupSavings {

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

}
