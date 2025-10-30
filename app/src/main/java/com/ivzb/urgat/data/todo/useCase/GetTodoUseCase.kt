package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.data.base.BaseGetUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import javax.inject.Inject

class GetTodoUseCase @Inject constructor(
    todoRepository: TodoRepository
): BaseGetUseCase<Todo>(todoRepository)
