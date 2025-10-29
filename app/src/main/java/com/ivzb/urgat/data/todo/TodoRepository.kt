package com.ivzb.urgat.data.todo

import com.ivzb.urgat.data.base.BaseRepository

interface TodoRepository: BaseRepository<Todo> {

    suspend fun create(title: String, description: String): String

    suspend fun update(id: String, title: String, description: String)

    suspend fun complete(id: String)

    suspend fun activate(id: String)

    suspend fun clearCompleted()

}
