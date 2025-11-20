package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface CompleteTodoUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultCompleteTodoUseCase @Inject constructor(
    private val repository: TodoRepository
): CompleteTodoUseCase {

    override suspend operator fun invoke(id: String) {
        repository.complete(id)
    }

}
