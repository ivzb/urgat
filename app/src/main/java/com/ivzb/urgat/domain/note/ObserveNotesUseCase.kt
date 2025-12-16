package com.ivzb.urgat.domain.note

import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.domain.default.DefaultObserveUseCase
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveNotesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Note>>>

}

class DefaultObserveNotesUseCase @Inject constructor(
    repository: NotesRepository
): ObserveNotesUseCase {

    private val defaultObserveUseCase = DefaultObserveUseCase<Note, Unit>(
        repository = repository,
        errorMessageResId = R.string.error_notes,
    )

    override fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<Unit>?,
        limitFlow: Flow<Int>?
    ): Flow<Async<List<Note>>> = defaultObserveUseCase(searchQueryFlow, filterTypeFlow, limitFlow)

}