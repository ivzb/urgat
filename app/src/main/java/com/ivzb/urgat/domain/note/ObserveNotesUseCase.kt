package com.ivzb.urgat.domain.note

import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.domain.common.ObserveUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveNotesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>? = null,
        filterTypeFlow: Flow<Unit>? = null,
        limitFlow: Flow<Int>? = null,
    ): Flow<Async<List<Note>>>

}

class DefaultObserveNotesUseCase @Inject constructor(
    repository: NotesRepository
): ObserveNotesUseCase {

    private val observeUseCase = ObserveUseCase<Note, Unit>(
        repository = repository,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>?,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?,
    ): Flow<Async<List<Note>>> = observeUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}