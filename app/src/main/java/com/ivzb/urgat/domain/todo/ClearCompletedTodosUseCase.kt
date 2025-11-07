package com.ivzb.urgat.domain.todo

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
