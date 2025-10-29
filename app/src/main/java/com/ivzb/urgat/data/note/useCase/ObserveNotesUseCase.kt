package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseObserveUseCase
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.ui.notes.NotesFilterType
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(
    notesRepository: NotesRepository
): BaseObserveUseCase<Note, NotesFilterType>(notesRepository) {

    override val errorMessage: Int = R.string.error_notes

    override fun filter(items: List<Note>, filteringType: NotesFilterType): List<Note> {
        return items
    }

}