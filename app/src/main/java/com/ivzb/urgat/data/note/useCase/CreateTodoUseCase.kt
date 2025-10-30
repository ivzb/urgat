package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

class CreateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke(name: String, description: String) {
        todoRepository.create(name, description)
    }

}
