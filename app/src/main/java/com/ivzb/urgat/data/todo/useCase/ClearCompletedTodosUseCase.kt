package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface ClearCompletedTodosUseCase {

    suspend operator fun invoke()

}

class DefaultClearCompletedTodosUseCase @Inject constructor(
    private val todoRepository: TodoRepository
): ClearCompletedTodosUseCase {

    override suspend operator fun invoke() {
        todoRepository.clearCompleted()
    }

}
