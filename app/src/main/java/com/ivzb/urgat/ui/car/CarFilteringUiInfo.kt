package com.ivzb.urgat.ui.car

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class CarFilteringUiInfo(
    override val emptyLabel: Int = R.string.no_fuel_consumption,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_expenses,
    override val emptyMessage: Int = 0,
    override val emptyButtonLabel: Int = 0,

    override val loadingLabel: Int = R.string.loading_car,

    override val errorLabel: Int = R.string.error_car,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
) : FilteringUiInfo {

    override val hasAppliedFilters: Boolean = false

}

