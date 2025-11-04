package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface ActivateTodoUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultActivateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
): ActivateTodoUseCase {

    override suspend operator fun invoke(id: String) {
        todoRepository.activate(id)
    }

}
