package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.todos.TodosFilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveTodosUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodosFilterMenuItem?>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Todo>>>

}

class DefaultObserveTodosUseCase @Inject constructor(
    repository: TodoRepository
): ObserveTodosUseCase, BaseObserveUseCase<Todo, TodosFilterMenuItem?>(repository) {

    override val errorMessage: Int = R.string.error_todos

    override fun filter(items: List<Todo>, filteringType: TodosFilterMenuItem?): List<Todo> {
        val type = filteringType?.type ?: TodosFilterType.ALL_TODOS

        if (type == TodosFilterType.ALL_TODOS) {
            return items
        }

        return items.filter {
            when (type) {
                TodosFilterType.ALL_TODOS -> true
                TodosFilterType.ACTIVE_TODOS -> it.isActive
                TodosFilterType.COMPLETED_TODOS -> it.isCompleted
            }
        }
    }

}