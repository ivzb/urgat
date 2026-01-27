package com.ivzb.urgat.ui.library

import androidx.annotation.StringRes
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.Page

sealed class LibraryPage(
    @StringRes
    override val titleResId: Int,
    override val isHorizontalScrollEnabled: Boolean,
): Page {
    data object Todos: LibraryPage(R.string.todos, true)
    data object Notes: LibraryPage(R.string.notes, true)
}

val LIBRARY_PAGES = listOf(
    LibraryPage.Todos,
    LibraryPage.Notes,
)