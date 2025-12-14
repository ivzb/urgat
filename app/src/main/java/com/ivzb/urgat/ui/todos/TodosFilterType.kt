package com.ivzb.urgat.ui.todos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TodosFilterType: Parcelable {
    ALL_TODOS,
    ACTIVE_TODOS,
    COMPLETED_TODOS
}