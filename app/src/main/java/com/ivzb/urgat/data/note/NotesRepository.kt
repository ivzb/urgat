package com.ivzb.urgat.data.note

import com.ivzb.urgat.data.base.BaseRepository

interface NotesRepository: BaseRepository<Note> {

    suspend fun create(content: String): String

    suspend fun update(id: String, content: String)

}
