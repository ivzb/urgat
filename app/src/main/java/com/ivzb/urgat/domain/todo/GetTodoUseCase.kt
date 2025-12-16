package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface GetTodoUseCase {

    suspend operator fun invoke(id: String): Todo?

}

class DefaultGetTodoUseCase @Inject constructor(
    private val repository: TodoRepository
): GetTodoUseCase {

    override suspend fun invoke(id: String): Todo? {
        return repository.get(id)
    }

}
