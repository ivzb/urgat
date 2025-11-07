package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.todo.TodosFilterType
import com.ivzb.urgat.ui.todo.TodosFilterType.ACTIVE_TODOS
import com.ivzb.urgat.ui.todo.TodosFilterType.ALL_TODOS
import com.ivzb.urgat.ui.todo.TodosFilterType.COMPLETED_TODOS
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


interface ObserveTodosUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodosFilterType>?,
        limit: Int
    ): Flow<Async<List<Todo>>>

}

class DefaultObserveTodosUseCase @Inject constructor(
    todoRepository: TodoRepository
): ObserveTodosUseCase, BaseObserveUseCase<Todo, TodosFilterType>(todoRepository) {

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