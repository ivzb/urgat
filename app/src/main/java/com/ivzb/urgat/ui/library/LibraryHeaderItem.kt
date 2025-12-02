package com.ivzb.urgat.ui.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.HeaderItem

typealias LibraryHeaderItem = HeaderItem<LibraryHeaderItemId>

val LIBRARY_HEADER_ITEMS = listOf(
    HeaderItem(
        LibraryHeaderItemId.Todo,
        Icons.Filled.Done,
        R.string.add_todo
    ),
    HeaderItem(
        LibraryHeaderItemId.Note,
        Icons.Filled.Person,
        R.string.add_note
    ),
)