package com.ivzb.urgat.ui.notes

sealed class NotesIntent {
    data class SearchQueryChanged(val query: String): NotesIntent()
}
