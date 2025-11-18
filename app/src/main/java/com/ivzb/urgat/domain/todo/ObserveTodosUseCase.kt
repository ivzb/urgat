package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.todo.Todo
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER_ID
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase.Companion.ACTIVE_FILTER_ID
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase.Companion.COMPLETED_FILTER_ID
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveTodosUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterMenuItem?>?,
        limit: Int
    ): Flow<Async<List<Todo>>>

    companion object {

        // todo: move to common place
        const val ACTIVE_FILTER_ID = "active"
        const val COMPLETED_FILTER_ID = "completed"
    }

}

class DefaultObserveTodosUseCase @Inject constructor(
    todoRepository: TodoRepository
): ObserveTodosUseCase, BaseObserveUseCase<Todo, FilterMenuItem?>(todoRepository) {

    override val errorMessage: Int = R.string.error_todos

    override fun filter(items: List<Todo>, filteringType: FilterMenuItem?): List<Todo> {
        if (filteringType == null || filteringType.id == CLEAR_FILTER_ID) {
            return items
        }

        return items.filter {
            when (filteringType.id) {
                ACTIVE_FILTER_ID -> it.isActive
                COMPLETED_FILTER_ID -> it.isCompleted
                else -> error("unsupported todos filtering type $filteringType")
            }
        }
    }

}