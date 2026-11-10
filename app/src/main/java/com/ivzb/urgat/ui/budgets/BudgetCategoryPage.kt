package com.ivzb.urgat.ui.budgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.GroupedBudget
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.ui.components.charts.PieChart
import com.ivzb.urgat.ui.components.charts.PieChartItem
import com.ivzb.urgat.util.annotateChart
import com.ivzb.urgat.util.annotateExpenses
import com.ivzb.urgat.util.annotateFinance
import java.math.BigDecimal

fun LazyListScope.budgetCategoryPage(
    budget: GroupedBudget,
    group: String? = null,
) {

    item {
        BudgetCategoryChart(
            budget = budget,
            group = group,
        )
    }

    item {
        Spacer(
            modifier = Modifier
                .height(dimensionResource(R.dimen.vertical_margin))
        )
    }

    budgetCategoryItems(budget)
}

@Composable
private fun BudgetCategoryChart(
    budget: GroupedBudget,
    group: String?,
) {
    val data = buildList {
        add(
            PieChartItem(
                value = budget.totalExpenses.toFloat(),
                title = stringResource(R.string.expenses),
                total = annotateExpenses(budget.totalExpenses, budget.currency),
            )
        )

        add(
            PieChartItem(
                value = budget.totalInvestments.toFloat(),
                title = stringResource(R.string.investments),
                total = annotateFinance(budget.totalInvestments, budget.currency),
            )
        )

        add(
            PieChartItem(
                value = budget.totalSavings.toFloat(),
                title = stringResource(R.string.savings),
                total = annotateFinance(budget.totalSavings, budget.currency),
            )
        )

        if (budget.totalTrades > BigDecimal.ZERO) {
            add(
                PieChartItem(
                    value = budget.totalTrades.toFloat(),
                    title = stringResource(R.string.trades),
                    total = annotateFinance(budget.totalTrades, budget.currency),
                )
            )
        }

        if (budget.remaining > BigDecimal.ZERO) {
            add(
                PieChartItem(
                    value = budget.remaining.toFloat(),
                    title = stringResource(R.string.remaining),
                    total = annotateFinance(budget.remaining, budget.currency)
                )
            )
        }
    }

    val total = annotateFinance(budget.total, budget.currency)
    val defaultText = annotateChart(group ?: budget.category.group, total)

    PieChart(
        data = data,
        defaultText = defaultText,
        textStyle = MaterialTheme.typography.titleLarge,
    )
}

private fun LazyListScope.budgetCategoryItems(
    budget: GroupedBudget,
) {
    item {
        CategoryItem(
            name = stringResource(R.string.expenses),
            value = annotateExpenses(budget.totalExpenses, budget.currency),
            colorIndex = 0,
        )
    }

    item {
        CategoryItem(
            name = stringResource(R.string.investments),
            value = annotateFinance(budget.totalInvestments, budget.currency),
            colorIndex = 1,
        )
    }

    item {
        CategoryItem(
            name = stringResource(R.string.savings),
            value = annotateFinance(budget.totalSavings, budget.currency),
            colorIndex = 2,
        )
    }

    val withTrades = budget.totalTrades > BigDecimal.ZERO

    if (withTrades) {
        item {
            CategoryItem(
                name = stringResource(R.string.trades),
                value = annotateFinance(budget.totalTrades, budget.currency),
                colorIndex = 3,
            )
        }
    }

    item {
        CategoryItem(
            name = stringResource(R.string.remaining),
            value = annotateFinance(budget.remaining, budget.currency),
            colorIndex = if (withTrades) 4 else 3,
        )
    }
}

@Composable
private fun CategoryItem(
    name: String,
    value: AnnotatedString,
    colorIndex: Int,
) {
    ListItem(
        textLeft = name,
        subTextRight = value,
        colorIndex = colorIndex,
    )
}
