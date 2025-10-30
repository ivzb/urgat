package com.ivzb.urgat.data.note.useCase

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) {

    suspend operator fun invoke(id: String, title: String, description: String) {
        todoRepository.update(id, title, description)
    }

}
