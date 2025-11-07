package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface UpdateTodoUseCase {

    suspend operator fun invoke(id: String, title: String, description: String)

}

class DefaultUpdateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
): UpdateTodoUseCase {

    override suspend operator fun invoke(id: String, title: String, description: String) {
        todoRepository.update(id, title, description)
    }

}