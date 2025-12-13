package com.ivzb.urgat.ui.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.FabAction

typealias LibraryFabAction = FabAction<LibraryFabActionId>

val LIBRARY_FAB_ACTIONS = listOf(
    FabAction(
        id = LibraryFabActionId.Todo,
        icon = Icons.Filled.Done,
        labelResId = R.string.add_todo,
    ),
    FabAction(
        id = LibraryFabActionId.Note,
        icon = Icons.Filled.Person,
        labelResId = R.string.add_note,
    )
)