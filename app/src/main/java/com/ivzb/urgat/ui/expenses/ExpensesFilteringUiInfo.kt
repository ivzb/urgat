package com.ivzb.urgat.ui.expenses

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class ExpensesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_expenses,

    override val emptyLabel: Int = R.string.no_expenses,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_expenses,

    override val errorLabel: Int = R.string.error_expenses,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
