package com.ivzb.urgat.ui.overview

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class OverviewFilteringUiInfo(
    override val emptyLabel: Int = 0,
    override val emptyIconRes: Int = 0,

    override val loadingLabel: Int = R.string.loading_overview,

    override val errorLabel: Int = R.string.error_overview,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
