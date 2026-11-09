package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.di.DefaultDispatcher
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterMenuItem
import com.ivzb.urgat.ui.todos.TodosFilterType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.Tracker
import javax.inject.Inject
import kotlin.collections.listOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface ObserveTodosFilteringUseCase {

    operator fun invoke(): Flow<Async<List<TodosFilterMenuItem>>>

}

class DefaultObserveTodosFilteringUseCase @Inject constructor(
    private val repository: TodoRepository,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ObserveTodosFilteringUseCase {

    override fun invoke(): Flow<Async<List<TodosFilterMenuItem>>> = repository.observe(NO_ITEMS_LIMIT, NO_DATE_RANGE)
        .map {
            listOf(
                FilterMenuItem(type = TodosFilterType.ALL_TODOS, labelResId = R.string.filter_all, size = it.size),
                FilterMenuItem(type = TodosFilterType.ACTIVE_TODOS, labelResId = R.string.filter_active, size = it.count { it.isActive }),
                FilterMenuItem(type = TodosFilterType.COMPLETED_TODOS, labelResId = R.string.filter_completed, size = it.count { it.isCompleted }),
            )
        }
        .map<List<TodosFilterMenuItem>, Async<List<TodosFilterMenuItem>>> { Async.Success(it) }
        .catch { e ->
            Tracker.error(e)
            emit(Async.Error)
        }
        .flowOn(dispatcher)

}
