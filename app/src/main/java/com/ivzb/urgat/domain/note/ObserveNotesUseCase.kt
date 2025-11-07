package com.ivzb.urgat.domain.note

import com.ivzb.urgat.R
import com.ivzb.urgat.domain.base.BaseObserveUseCase
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.ui.notes.NotesFilterType
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ObserveNotesUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
        filterTypeFlow: Flow<NotesFilterType>?,
        limit: Int
    ): Flow<Async<List<Note>>>

}

class DefaultObserveNotesUseCase @Inject constructor(
    notesRepository: NotesRepository
): ObserveNotesUseCase, BaseObserveUseCase<Note, NotesFilterType>(notesRepository) {

    override val errorMessage: Int = R.string.error_notes

    override fun filter(items: List<Note>, filteringType: NotesFilterType): List<Note> {
        return items
    }

}