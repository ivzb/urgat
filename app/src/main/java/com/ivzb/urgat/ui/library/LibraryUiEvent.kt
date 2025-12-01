package com.ivzb.urgat.ui.library

sealed class LibraryUiEvent {
    object AddTodo : LibraryUiEvent()
    object AddNote : LibraryUiEvent()
}
