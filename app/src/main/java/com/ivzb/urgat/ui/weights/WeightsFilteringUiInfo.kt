package com.ivzb.urgat.ui.weights

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class WeightsFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.recent_weights,

    override val emptyLabel: Int = R.string.no_weights_label,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_weights,
    override val emptyMessage: Int = R.string.no_weights_message,
    override val emptyButtonLabel: Int = R.string.add_weight,

    override val loadingLabel: Int = R.string.loading_weights,

    override val errorLabel: Int = R.string.error_weights,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",

    val sortMenuItems: List<WeightsSortMenuItem> = listOf(),
    val showSortBadge: Boolean = false,
) : FilteringUiInfo {

    override val hasAppliedFilters: Boolean = false

}
