package com.ivzb.urgat.domain.base

import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

abstract class BaseObserveUseCase<T, FilterType>(
    private val repository: BaseRepository<T>
) {

    protected abstract val errorMessage: Int

    private val noLimitFlow = MutableStateFlow(NO_ITEMS_LIMIT)

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<T>>> = combine(
        searchQueryFlow,
        limitFlow ?: noLimitFlow
    ) { searchQuery, limit ->
            Pair(searchQuery, limit)
        }
        .debounce(DEBOUNCE_TIME_MILLIS)
        .flatMapLatest { (searchQuery, limit) ->
            val stream = if (searchQuery.isBlank()) {
                repository.getStream(limit)
            } else {
                repository.getStream(limit, searchQuery)
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
        .catch { e ->
            // todo: use error tracker instead

            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }

            emit(Async.Error(errorMessage, e))
        }

    protected open fun filter(items: List<T>, filteringType: FilterType): List<T> = items

    protected open fun transform(items: List<T>): List<T> = items

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }
}