package com.ivzb.urgat.ui.notes

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class NotesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_notes,

    override val emptyLabel: Int = R.string.no_notes,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_notes,

    override val errorLabel: Int = R.string.error_notes,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
