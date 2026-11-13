package com.ivzb.urgat.navigation

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.addEditNote.AddEditNoteRoute

fun UrgatNavActions.navigateToAddNote() {
    navigator.navigate(
        AddEditNoteRoute(
            id = null,
            titleResId = R.string.add_note
        )
    )
}

fun UrgatNavActions.navigateToEditNote(noteId: String) {
    navigator.navigate(
        AddEditNoteRoute(
            id = noteId,
            titleResId = R.string.edit_note
        )
    )
}
