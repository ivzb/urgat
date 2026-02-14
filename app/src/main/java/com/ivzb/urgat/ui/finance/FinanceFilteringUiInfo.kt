package com.ivzb.urgat.ui.finance

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class FinanceFilteringUiInfo(
    override val emptyLabel: Int = 0,
    override val emptyIconRes: Int = 0,
    override val emptyMessage: Int = 0,
    override val emptyButtonLabel: Int = 0,

    override val loadingLabel: Int = R.string.loading_finance,

    override val errorLabel: Int = R.string.error_finance,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
