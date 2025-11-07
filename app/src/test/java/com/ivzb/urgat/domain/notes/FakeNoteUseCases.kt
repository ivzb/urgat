package com.ivzb.urgat.domain.notes

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.domain.note.CreateNoteUseCase
import com.ivzb.urgat.domain.note.GetNoteUseCase
import com.ivzb.urgat.domain.note.UpdateNoteUseCase

class FakeGetNoteUseCase : GetNoteUseCase {

    var toReturn: Note? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Note? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateNoteUseCase : CreateNoteUseCase {

    var created: String? = null

    override suspend fun invoke(content: String) {
        created = content
    }
}

class FakeUpdateNoteUseCase : UpdateNoteUseCase {

    var updated: Pair<String, String>? = null

    override suspend fun invoke(id: String, content: String) {
        updated = Pair(id, content)
    }
}
