package com.ivzb.urgat.data.todo

import com.ivzb.urgat.data.base.BaseRepository

interface TodoRepository: BaseRepository<Todo> {

    suspend fun create(content: String): String

    suspend fun update(id: String, content: String)

    suspend fun complete(id: String)

    suspend fun activate(id: String)

    suspend fun clearCompleted()

}
