package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface UpdateTodoUseCase {

    suspend operator fun invoke(id: String, content: String)

}

class DefaultUpdateTodoUseCase @Inject constructor(
    private val repository: TodoRepository
): UpdateTodoUseCase {

    override suspend operator fun invoke(id: String, content: String) {
        repository.update(id, content)
    }

}