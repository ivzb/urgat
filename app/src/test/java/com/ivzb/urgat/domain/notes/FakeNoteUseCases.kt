package com.ivzb.urgat.domain.notes

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.note.GetNoteUseCase
import com.ivzb.urgat.domain.note.UpdateNoteUseCase

class FakeGetNoteUseCase(
    private val note: Note? = null
) : GetNoteUseCase {

    var calledWithId: String? = null

    override suspend fun invoke(id: String): Note? {
        calledWithId = id
        return note
    }
}

class FakeCreateNoteUseCase : CreateNoteUseCase {

    val created = mutableListOf<String>()

    override suspend fun invoke(content: String) {
        created += content
    }
}

class FakeUpdateNoteUseCase : UpdateNoteUseCase {

    val updated = mutableListOf<Pair<String, String>>()

    override suspend fun invoke(id: String, content: String) {
        updated += Pair(id, content)
    }
}
