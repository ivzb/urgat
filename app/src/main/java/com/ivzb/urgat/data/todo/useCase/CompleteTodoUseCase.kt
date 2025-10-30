package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

class CompleteTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke(id: String) {
        todoRepository.complete(id)
    }

}
