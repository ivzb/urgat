package com.ivzb.urgat.ui.components

interface FilteringUiInfo {

    val emptyLabel: Int
    val emptyIconRes: Int
    val emptyMessage: Int
    val emptyButtonLabel: Int

    val loadingLabel: Int

    val errorLabel: Int
    val errorIconRes: Int

    val searchQuery: String

    val hasAppliedFilters: Boolean

}