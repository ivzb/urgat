package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveTodosUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodosFilterType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Todo>>>

}

class DefaultObserveTodosUseCase @Inject constructor(
    repository: TodoRepository
): ObserveTodosUseCase {

    private val observeUseCase = ObserveUseCase(
        repository = repository,
        filter = ::filter,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<TodosFilterType>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Todo>>> = observeUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

    companion object {

        private fun filter(items: List<Todo>, filterType: TodosFilterType): List<Todo> {
            if (filterType == TodosFilterType.ALL_TODOS) {
                return items
            }

            return items.filter {
                when (filterType) {
                    TodosFilterType.ALL_TODOS -> true
                    TodosFilterType.ACTIVE_TODOS -> it.isActive
                    TodosFilterType.COMPLETED_TODOS -> it.isCompleted
                }
            }
        }

    }

}