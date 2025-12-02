package com.ivzb.urgat.ui.weights

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class WeightsFilteringUiInfo(
    override val emptyLabel: Int = R.string.no_weights_label,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_weights,
    val emptyMessage: Int = R.string.no_weights_message,
    val emptyButtonLabel: Int = R.string.track_weight,

    override val loadingLabel: Int = R.string.loading_weights,

    override val errorLabel: Int = R.string.error_weights,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
