package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.base.BaseGetUseCase
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    notesRepository: NotesRepository
): BaseGetUseCase<Note>(notesRepository)