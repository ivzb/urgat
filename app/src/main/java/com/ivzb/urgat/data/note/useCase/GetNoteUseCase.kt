package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.base.BaseGetUseCase
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

interface GetNoteUseCase {

    suspend operator fun invoke(id: String): Note?

}

class DefaultGetNoteUseCase @Inject constructor(
    notesRepository: NotesRepository
): GetNoteUseCase, BaseGetUseCase<Note>(notesRepository)