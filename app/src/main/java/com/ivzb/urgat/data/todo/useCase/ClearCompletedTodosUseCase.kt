package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

class ClearCompletedTodosUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke() {
        todoRepository.clearCompleted()
    }

}
