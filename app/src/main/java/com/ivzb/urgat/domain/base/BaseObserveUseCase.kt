package com.ivzb.urgat.domain.base

import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

abstract class BaseObserveUseCase<T, FilterType>(
    private val repository: BaseRepository<T>
) {

    protected abstract val errorMessage: Int

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterType>? = null,
        limit: Int = NO_LIMIT
    ): Flow<Async<List<T>>> = searchQueryFlow
        .debounce(DEBOUNCE_TIME_MILLIS)
        .flatMapLatest { query ->
            val stream = if (query.isBlank()) {
                repository.getStream(limit)
            } else {
                repository.getStream(limit, query)
            }

            if (filterTypeFlow == null) {
                stream
            } else {
                combine(stream, filterTypeFlow) { items, filteringType ->
                    filter(items, filteringType)
                }
            }
            .map(::transform)
        }
        .map<List<T>, Async<List<T>>> { Async.Success(it) }
        .catch { emit(Async.Error(errorMessage)) }

    protected open fun filter(items: List<T>, filteringType: FilterType): List<T> = items

    protected open fun transform(items: List<T>): List<T> = items

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }
}