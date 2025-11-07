package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface CreateTodoUseCase {

    suspend operator fun invoke(name: String, description: String)

}

class DefaultCreateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
): CreateTodoUseCase {

    override suspend operator fun invoke(name: String, description: String) {
        todoRepository.create(name, description)
    }

}