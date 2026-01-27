package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface CreateTodoUseCase {

    suspend operator fun invoke(content: String)

}

class DefaultCreateTodoUseCase @Inject constructor(
    private val repository: TodoRepository
): CreateTodoUseCase {

    override suspend operator fun invoke(content: String) {
        repository.create(content)
    }

}