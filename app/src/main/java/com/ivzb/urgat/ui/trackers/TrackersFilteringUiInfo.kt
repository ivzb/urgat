package com.ivzb.urgat.ui.trackers

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class TrackersFilteringUiInfo(
    override val emptyLabel: Int = R.string.no_weights,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_trackers,

    override val errorLabel: Int = R.string.error_trackers,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
