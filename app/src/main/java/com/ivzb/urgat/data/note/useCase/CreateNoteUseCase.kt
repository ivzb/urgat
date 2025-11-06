package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

interface CreateNoteUseCase {

    suspend operator fun invoke(content: String)

}

class DefaultCreateNoteUseCase @Inject constructor(
    private val notesRepository: NotesRepository
): CreateNoteUseCase {

    override suspend operator fun invoke(content: String) {
        notesRepository.create(content)
    }

}
