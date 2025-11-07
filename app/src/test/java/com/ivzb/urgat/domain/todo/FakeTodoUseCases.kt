package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.Todo

class FakeGetTodoUseCase(
    private val todo: Todo? = null
) : GetTodoUseCase {

    var calledWithId: String? = null

    override suspend fun invoke(id: String): Todo? {
        calledWithId = id
        return todo
    }
}

class FakeCreateTodoUseCase : CreateTodoUseCase {

    val created = mutableListOf<Pair<String, String>>()

    override suspend fun invoke(name: String, description: String) {
        created += name to description
    }
}

class FakeUpdateTodoUseCase : UpdateTodoUseCase {

    val updated = mutableListOf<Triple<String, String, String>>()

    override suspend fun invoke(id: String, title: String, description: String) {
        updated += Triple(id, title, description)
    }
}
