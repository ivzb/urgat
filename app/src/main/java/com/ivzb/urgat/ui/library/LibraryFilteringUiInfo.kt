package com.ivzb.urgat.ui.library

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class LibraryFilteringUiInfo(
    override val emptyLabel: Int = 0,
    override val emptyIconRes: Int = 0,
    override val emptyMessage: Int = 0,
    override val emptyButtonLabel: Int = 0,

    override val loadingLabel: Int = R.string.loading_library,

    override val errorLabel: Int = R.string.error_library,
    override val errorIconRes: Int = R.drawable.ic_error,

    override val searchQuery: String = "",
) : FilteringUiInfo {

    override val hasAppliedFilters: Boolean = false

}
