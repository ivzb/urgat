package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.domain.base.BaseGetUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

interface GetTodoUseCase {

    suspend operator fun invoke(id: String): Todo?

}

class DefaultGetTodoUseCase @Inject constructor(
    todoRepository: TodoRepository
): GetTodoUseCase, BaseGetUseCase<Todo>(todoRepository)
