package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class InvestmentsFilteringUiInfo(
    override val emptyLabel: Int = R.string.no_investments,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_investments,

    override val errorLabel: Int = R.string.error_investments,
    override val errorIconRes: Int = R.drawable.ic_error,

    val filterMenuItems: List<InvestmentsFilterMenuItem> = listOf()
) : FilteringUiInfo
