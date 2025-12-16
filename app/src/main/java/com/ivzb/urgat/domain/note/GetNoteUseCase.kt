package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import javax.inject.Inject

interface GetNoteUseCase {

    suspend operator fun invoke(id: String): Note?

}

class DefaultGetNoteUseCase @Inject constructor(
    private val repository: NotesRepository
): GetNoteUseCase {

    override suspend fun invoke(id: String): Note? {
        return repository.get(id)
    }

}