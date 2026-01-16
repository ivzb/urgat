package com.ivzb.urgat.ui.budgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivzb.urgat.R
import com.ivzb.urgat.data.budget.Budget
import com.ivzb.urgat.ui.components.ListItem
import com.ivzb.urgat.util.format
import com.ivzb.urgat.util.toLocalDate

@Composable
fun BudgetItem(
    budget: Budget,
    onBudgetClick: (Budget) -> Unit
) {
    ListItem(
        textLeft = budget.income.format(),
        subTextLeft = budget.date.toLocalDate().format(),

        textRight = stringResource(R.string.bank_start_format, budget.bankStart.format()),
        subTextRight = budget.bankEnd?.let { stringResource(R.string.bank_end_format, it.format()) }.orEmpty(),

        onClick = { onBudgetClick(budget) }
    )
}