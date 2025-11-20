package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface DeleteTodoUseCase {

    suspend operator fun invoke(id: String)

}

class DefaultDeleteTodoUseCase @Inject constructor(
    private val repository: TodoRepository
): DeleteTodoUseCase {

    override suspend operator fun invoke(id: String) {
        repository.delete(id)
    }

}