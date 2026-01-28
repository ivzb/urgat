package com.ivzb.urgat.domain.common

import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_DATE_RANGE
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_SEARCH_QUERY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

abstract class DefaultObserveUseCase<T, FilterType, SortType>(
    private val repository: BaseRepository<T>,
    private val filter: ((List<T>, FilterType) -> List<T>)? = null,
    private val sort: ((List<T>, SortType) -> List<T>)? = null,
    private val transform: ((List<T>) -> List<T>)? = null,
) {

    private val noSearchFlow = MutableStateFlow(NO_SEARCH_QUERY)
    private val noDateRangeFlow = MutableStateFlow(NO_DATE_RANGE)
    private val noLimitFlow = MutableStateFlow(NO_ITEMS_LIMIT)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    protected fun invokeFlow(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<FilterType>? = null,
        sortTypeFlow: Flow<SortType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<List<T>> =
        combine(
            searchQueryFlow ?: noSearchFlow,
            dateRangeFlow ?: noDateRangeFlow,
            limitFlow ?: noLimitFlow
        ) { searchQuery, dateRange, limit ->
            Triple(searchQuery, dateRange, limit)
        }
        .debounce(DEBOUNCE_TIME_MILLIS)
        .flatMapLatest { (searchQuery, dateRange, limit) ->
            val stream = if (searchQuery.isBlank()) {
                repository.observe(limit, dateRange)
            } else {
                repository.observe(limit, dateRange, searchQuery)
            }

            combine(
                stream,
                filterTypeFlow ?: flowOf(null),
                sortTypeFlow ?: flowOf(null)
            ) { items, filterType, sortType ->
                var result = items

                if (filter != null && filterType != null) {
                    result = filter(result, filterType)
                }

                if (sort != null && sortType != null) {
                    result = sort(result, sortType)
                }

                if (transform != null) {
                    result = transform(result)
                }

                result
            }
        }

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }

}
