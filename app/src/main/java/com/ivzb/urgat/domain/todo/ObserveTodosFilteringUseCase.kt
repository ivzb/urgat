package com.ivzb.urgat.domain.todo

import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.todo.TodoRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase.Companion.CLEAR_FILTER_ID
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase.Companion.ACTIVE_FILTER_ID
import com.ivzb.urgat.domain.todo.ObserveTodosUseCase.Companion.COMPLETED_FILTER_ID
import com.ivzb.urgat.ui.components.FilterMenuItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.listOf

interface ObserveTodosFilteringUseCase {

    operator fun invoke(): Flow<Async<List<FilterMenuItem>>>

}

class DefaultObserveTodosFilteringUseCase @Inject constructor(
    private val todoRepository: TodoRepository
) : ObserveTodosFilteringUseCase {

    override fun invoke(): Flow<Async<List<FilterMenuItem>>> {
        return todoRepository.getStream(NO_LIMIT)
            .map {
                listOf(
                    FilterMenuItem(id = CLEAR_FILTER_ID, labelResId = R.string.filter_all, size = it.size),
                    FilterMenuItem(id = ACTIVE_FILTER_ID, labelResId = R.string.filter_active, size = it.count { it.isActive }),
                    FilterMenuItem(id = COMPLETED_FILTER_ID, labelResId = R.string.filter_completed, size = it.count { it.isCompleted }),
                )
            }
            .map<List<FilterMenuItem>, Async<List<FilterMenuItem>>> { Async.Success(it) }
            .catch { emit(Async.Error(R.string.error_todos_filtering)) }
    }

}
