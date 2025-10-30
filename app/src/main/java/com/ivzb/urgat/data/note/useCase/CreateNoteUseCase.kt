package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
) {

    suspend operator fun invoke(content: String) {
        notesRepository.create(content)
    }

}
