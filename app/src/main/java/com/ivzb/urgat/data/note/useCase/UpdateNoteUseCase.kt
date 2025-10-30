package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    suspend operator fun invoke(id: String, content: String) {
        notesRepository.update(id, content)
    }

}
