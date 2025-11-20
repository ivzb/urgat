package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

interface UpdateNoteUseCase {

    suspend operator fun invoke(id: String, content: String)

}

class DefaultUpdateNoteUseCase @Inject constructor(
    private val repository: NotesRepository
): UpdateNoteUseCase {

    override suspend operator fun invoke(id: String, content: String) {
        repository.update(id, content)
    }

}
