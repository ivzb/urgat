package com.ivzb.urgat.ui.investments

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class InvestmentsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_investments,

    override val emptyLabel: Int = R.string.no_investments,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_investments,
    override val emptyMessage: Int = R.string.no_investments_message,
    override val emptyButtonLabel: Int = R.string.add_investment,

    override val loadingLabel: Int = R.string.loading_investments,

    override val errorLabel: Int = R.string.error_investments,
    override val errorIconRes: Int = R.drawable.ic_error,

    val filterMenuItems: List<InvestmentsFilterMenuItem> = listOf(),
    val sortMenuItems: List<InvestmentsSortMenuItem> = listOf(),
) : FilteringUiInfo
