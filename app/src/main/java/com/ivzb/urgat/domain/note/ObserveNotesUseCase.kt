package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.util.Async
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ObserveNotesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        dateRangeFlow: Flow<LongRange>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        sortTypeFlow: Flow<Unit>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Note>>>

}

class DefaultObserveNotesUseCase @Inject constructor(
    repository: NotesRepository
): ObserveNotesUseCase {

    private val observeUseCase = ObserveUseCase<Note, Unit, Unit>(
        repository = repository,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        dateRangeFlow: Flow<LongRange>?,
        filterTypeFlow: Flow<Unit>?,
        sortTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Note>>> = observeUseCase(searchQueryFlow, dateRangeFlow, filterTypeFlow, sortTypeFlow, limitFlow)

}
