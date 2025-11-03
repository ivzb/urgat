package com.ivzb.urgat.ui.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class TodoFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_todos,

    override val emptyLabel: Int = R.string.no_todos,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,
    val emptyMessage: Int = R.string.no_todos_message,
    val emptyButtonLabel: Int = R.string.add_todo,

    override val loadingLabel: Int = R.string.loading_todos,

    override val errorLabel: Int = R.string.error_todos,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
