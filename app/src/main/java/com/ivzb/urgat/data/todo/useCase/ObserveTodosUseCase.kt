package com.ivzb.urgat.data.todo.useCase

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseObserveUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.todo.TodosFilterType
import com.ivzb.urgat.ui.todo.TodosFilterType.ACTIVE_TODOS
import com.ivzb.urgat.ui.todo.TodosFilterType.ALL_TODOS
import com.ivzb.urgat.ui.todo.TodosFilterType.COMPLETED_TODOS
import javax.inject.Inject

class ObserveTodosUseCase @Inject constructor(
    todoRepository: TodoRepository
): BaseObserveUseCase<Todo, TodosFilterType>(todoRepository) {

    override val errorMessage: Int = R.string.error_todos

    override fun filter(items: List<Todo>, filteringType: TodosFilterType): List<Todo> {
        if (filteringType == ALL_TODOS) {
            return items
        }

        return items.filter {
            when (filteringType) {
                ALL_TODOS -> true
                ACTIVE_TODOS -> it.isActive
                COMPLETED_TODOS -> it.isCompleted
            }
        }
    }

}