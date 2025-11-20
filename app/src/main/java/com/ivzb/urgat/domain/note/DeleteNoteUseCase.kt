package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

interface DeleteNoteUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteNoteUseCase @Inject constructor(
    private val repository: NotesRepository
): DeleteNoteUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}