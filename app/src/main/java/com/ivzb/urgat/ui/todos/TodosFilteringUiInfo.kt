package com.ivzb.urgat.ui.todos

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FilteringUiInfo

data class TodosFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_todos,

    override val emptyLabel: Int = R.string.no_todos,
    override val emptyIconRes: Int = R.drawable.ic_empty_state_card_todos,
    override val emptyMessage: Int = R.string.no_todos_message,
    override val emptyButtonLabel: Int = R.string.add_todo,

    override val loadingLabel: Int = R.string.loading_todos,

    override val errorLabel: Int = R.string.error_todos,
    override val errorIconRes: Int = R.drawable.ic_error,

    val filterMenuItems: List<TodosFilterMenuItem> = listOf()
) : FilteringUiInfo
