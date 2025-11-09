package com.ivzb.urgat.ui.overview

sealed class OverviewUiEvent {
    object AddTodo : OverviewUiEvent()
    object AddExpense : OverviewUiEvent()
    object AddNote : OverviewUiEvent()
}
