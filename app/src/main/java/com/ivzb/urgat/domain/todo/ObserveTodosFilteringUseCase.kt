package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.listOf

interface ObserveTodosFilteringUseCase {

    operator fun invoke(): Flow<Async<List<TodosFilterMenuItem>>>

}

class DefaultObserveTodosFilteringUseCase @Inject constructor(
    private val repository: TodoRepository
) : ObserveTodosFilteringUseCase {

    override fun invoke(): Flow<Async<List<TodosFilterMenuItem>>> {
        return repository.getStream(NO_ITEMS_LIMIT)
            .map {
                listOf(
                    FilterMenuItem(type = TodosFilterType.ALL_TODOS, labelResId = R.string.filter_all, size = it.size),
                    FilterMenuItem(type = TodosFilterType.ACTIVE_TODOS, labelResId = R.string.filter_active, size = it.count { it.isActive }),
                    FilterMenuItem(type = TodosFilterType.COMPLETED_TODOS, labelResId = R.string.filter_completed, size = it.count { it.isCompleted }),
                )
            }
            .map<List<TodosFilterMenuItem>, Async<List<TodosFilterMenuItem>>> { Async.Success(it) }
            .catch { e -> emit(Async.Error(R.string.error_todos_filtering, e)) }
    }

}
