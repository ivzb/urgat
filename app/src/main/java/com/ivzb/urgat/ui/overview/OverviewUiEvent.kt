package com.ivzb.urgat.ui.overview

sealed class OverviewUiEvent {
    class AddTodo() : OverviewUiEvent()
    class AddExpense() : OverviewUiEvent()
    class AddNote() : OverviewUiEvent()
}
