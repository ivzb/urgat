package com.ivzb.urgat.ui.wellbeing

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class WellbeingFilteringUiInfo(
    override val emptyLabel: Int = R.string.no_weights,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_weights,

    override val loadingLabel: Int = R.string.loading_wellbeing,

    override val errorLabel: Int = R.string.error_wellbeing,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
