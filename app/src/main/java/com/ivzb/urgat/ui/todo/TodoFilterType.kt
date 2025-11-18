package com.ivzb.urgat.ui.todo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TodoFilterType: Parcelable {
    ALL_TODOS,
    ACTIVE_TODOS,
    COMPLETED_TODOS
}