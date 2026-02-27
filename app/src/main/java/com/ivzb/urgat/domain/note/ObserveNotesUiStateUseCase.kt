package com.ivzb.urgat.domain.note

import com.ivzb.urgat.R
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.notes.NotesUiState
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.combineUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveNotesUiStateUseCase {

    operator fun invoke(
        searchQueryFlow: Flow<String>,
    ): Flow<Async<NotesUiState>>

}

class DefaultObserveNotesUiStateUseCase @Inject constructor(
    private val observeNotesUseCase: ObserveNotesUseCase,
): ObserveNotesUiStateUseCase {

    override fun invoke(
        searchQueryFlow: Flow<String>,
    ): Flow<Async<NotesUiState>> = combineUiState(
        observeNotesUseCase(searchQueryFlow, null, null, null, null),
        searchQueryFlow.map { Async.Success(it) },

        onLoading = {
            NotesUiState(isLoading = true)
        },
        onError = {
            NotesUiState(hasError = true)
        },
        onSuccess = { notes, searchQuery ->
            NotesUiState(
                notes = notes,
                isLoading = false,
                filteringUiInfo = NotesFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_notes,
                    emptyLabel = if (searchQuery.isEmpty()) R.string.no_notes_label else R.string.no_notes_search,
                    emptyMessage = R.string.no_notes_message,
                    emptyButtonLabel = R.string.add_note,
                    searchQuery = searchQuery,
                ),
            )
        }
    ).map { Async.Success(it) }

}