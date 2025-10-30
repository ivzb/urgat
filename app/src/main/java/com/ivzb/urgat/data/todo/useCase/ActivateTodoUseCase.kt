package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

class ActivateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke(id: String) {
        todoRepository.activate(id)
    }

}
