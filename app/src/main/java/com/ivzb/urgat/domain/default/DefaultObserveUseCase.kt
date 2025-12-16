package com.ivzb.urgat.domain.default

import androidx.annotation.StringRes
import com.ivzb.urgat.BuildConfig
import com.ivzb.urgat.data.base.BaseRepository
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class DefaultObserveUseCase<T, FilterType>(
    private val repository: BaseRepository<T>,
    private val filter: (List<T>, FilterType) -> List<T> = { items, _ -> items },
    private val transform: (List<T>) -> List<T> = { items -> items },
    @StringRes
    private val errorMessageResId: Int,
) {

    private val noLimitFlow = MutableStateFlow(NO_ITEMS_LIMIT)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<FilterType>? = null,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<T>>> =
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
                    filter(items, filterType)
                }
            }
            .map { items -> transform(items) }
        }
        .map<List<T>, Async<List<T>>> { Async.Success(it) }
        .catch { e ->
            // todo: use error tracker instead

            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }

            emit(Async.Error(errorMessageResId, e))
        }

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }

}