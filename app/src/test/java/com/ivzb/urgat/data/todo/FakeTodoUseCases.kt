package com.ivzb.urgat.data.todo

import com.ivzb.urgat.data.todo.useCase.CreateTodoUseCase
import com.ivzb.urgat.data.todo.useCase.GetTodoUseCase
import com.ivzb.urgat.data.todo.useCase.UpdateTodoUseCase

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
