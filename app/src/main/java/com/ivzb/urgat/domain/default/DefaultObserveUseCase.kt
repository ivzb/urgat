package com.ivzb.urgat.domain.default

import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

abstract class DefaultObserveUseCase<T, FilterType>(
    private val repository: BaseRepository<T>,
    private val filter: ((List<T>, FilterType) -> List<T>)? = null,
    private val transform: ((List<T>) -> List<T>)? = null,
) {

    private val noLimitFlow = MutableStateFlow(NO_ITEMS_LIMIT)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    protected fun invokeFlow(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<List<T>> =
        combine(
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
                combine(stream, filterTypeFlow) { items, filterType ->
                    if (filter == null) {
                        items
                    } else {
                        filter(items, filterType)
                    }
                }
            }
            .map { items ->
                if (transform == null) {
                    items
                } else {
                    transform(items)
                }
            }
        }

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }

}
