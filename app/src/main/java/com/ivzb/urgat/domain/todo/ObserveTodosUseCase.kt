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
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<TodosFilterType>? = null,
        sortTypeFlow: Flow<Unit>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Todo>>>

}

class DefaultObserveTodosUseCase @Inject constructor(
    repository: TodoRepository
): ObserveTodosUseCase {

    private val observeUseCase = ObserveUseCase<Todo, TodosFilterType, Unit>(
        repository = repository,
        filter = ::filter,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<TodosFilterType>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Todo>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

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