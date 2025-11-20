package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

interface CreateNoteUseCase {

    suspend operator fun invoke(content: String)

}

class DefaultCreateNoteUseCase @Inject constructor(
    private val repository: NotesRepository
): CreateNoteUseCase {

    override suspend operator fun invoke(content: String) {
        repository.create(content)
    }

}
