package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class ExpensesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_expenses,

    override val emptyLabel: Int = R.string.no_expenses_label,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,
    val emptyMessage: Int = R.string.no_expenses_message,
    val emptyButtonLabel: Int = R.string.add_expense,

    override val loadingLabel: Int = R.string.loading_expenses,

    override val errorLabel: Int = R.string.error_expenses,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
