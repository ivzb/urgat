package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface CompleteTodoUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultCompleteTodoUseCase @Inject constructor(
    private val todoRepository: TodoRepository
): CompleteTodoUseCase {

    override suspend operator fun invoke(id: String) {
        todoRepository.complete(id)
    }

}
