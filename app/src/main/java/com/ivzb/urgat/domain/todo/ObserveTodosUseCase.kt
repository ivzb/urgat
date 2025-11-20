package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.todo.TodoFilterMenuItem
import com.ivzb.urgat.ui.todo.TodoFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveTodosUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodoFilterMenuItem?>?,
        limit: Int
    ): Flow<Async<List<Todo>>>

}

class DefaultObserveTodosUseCase @Inject constructor(
    repository: TodoRepository
): ObserveTodosUseCase, BaseObserveUseCase<Todo, TodoFilterMenuItem?>(repository) {

    override val errorMessage: Int = R.string.error_todos

    override fun filter(items: List<Todo>, filteringType: TodoFilterMenuItem?): List<Todo> {
        val type = filteringType?.type ?: TodoFilterType.ALL_TODOS

        if (type == TodoFilterType.ALL_TODOS) {
            return items
        }

        return items.filter {
            when (type) {
                TodoFilterType.ALL_TODOS -> true
                TodoFilterType.ACTIVE_TODOS -> it.isActive
                TodoFilterType.COMPLETED_TODOS -> it.isCompleted
            }
        }
    }

}