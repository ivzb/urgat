package com.ivzb.urgat.ui.addEditNote

sealed class AddEditNoteIntent {
    data class UpdateContent(val value: String): AddEditNoteIntent()
    object SaveNote: AddEditNoteIntent()
    object DeleteNote: AddEditNoteIntent()
    object UserMessageShown: AddEditNoteIntent()
}