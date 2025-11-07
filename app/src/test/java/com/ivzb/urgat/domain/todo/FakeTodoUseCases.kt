package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.Todo

class FakeGetTodoUseCase : GetTodoUseCase {

    var toReturn: Todo? = null
    var calledWithId: String? = null

    override suspend fun invoke(id: String): Todo? {
        calledWithId = id
        return toReturn
    }
}

class FakeCreateTodoUseCase : CreateTodoUseCase {

    var created: Pair<String, String>? = null

    override suspend fun invoke(name: String, description: String) {
        created = name to description
    }
}

class FakeUpdateTodoUseCase : UpdateTodoUseCase {

    var updated: Triple<String, String, String>? = null

    override suspend fun invoke(id: String, title: String, description: String) {
        updated = Triple(id, title, description)
    }
}
