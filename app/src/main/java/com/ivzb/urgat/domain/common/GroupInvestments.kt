package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentAsset
import com.ivzb.urgat.data.investment.InvestmentAverage
import com.ivzb.urgat.data.investment.InvestmentCategory
import com.ivzb.urgat.data.investment.InvestmentNetWorth
import com.ivzb.urgat.domain.common.Group.byDateYearly
import com.ivzb.urgat.domain.investment.GroupedInvestmentCategories
import com.ivzb.urgat.ui.components.GroupedItems
import kotlin.collections.component1
import kotlin.collections.component2

object GroupInvestments {

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

    /**
     * Average monthly investments of each year, keyed by the yearly group.
     */
    fun groupInvestmentAveragesYearly(investments: List<Investment>): Map<String, InvestmentAverage> =
        byDateYearly(investments, Investment::date, ::investmentAverage)
            .associate { (group, average, _) -> group to average }

    fun groupInvestmentAverageAllTime(investments: List<Investment>): InvestmentAverage =
        investmentAverage(investments)

    /**
     * Averages are spread over the months of the whole period,
     * so every category and asset is comparable within the same period.
     *
     * A single month has no meaningful average, as it is the same as its total.
     */
    private fun investmentAverage(investments: List<Investment>): InvestmentAverage {
        val months = Investment.months(investments)

        if (months <= 1) {
            return InvestmentAverage.EMPTY
        }

        return InvestmentAverage(
            totals = Investment.averageMonthly(investments, months),
            categories = investments
                .groupBy { it.category }
                .mapValues { (_, categoryInvestments) -> Investment.averageMonthly(categoryInvestments, months) },
            assets = investments
                .groupBy { it.name }
                .mapValues { (_, assetInvestments) -> Investment.averageMonthly(assetInvestments, months) },
        )
    }

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

}
