package com.ivzb.urgat.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    observeNotesUseCase: ObserveNotesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<NotesUiState> = combine(
        _isLoading,
        observeNotesUseCase(_searchQuery, null, NO_LIMIT)
    ) { isLoading, notesAsync ->
        when (notesAsync) {
            Async.Loading -> {
                NotesUiState(isLoading = true)
            }

            is Async.Error -> {
                NotesUiState(hasError = true)
            }

            is Async.Success -> {
                NotesUiState(
                    items = notesAsync.data,
                    filteringUiInfo = NotesFilteringUiInfo(),
                    isLoading = isLoading
                )
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = NotesUiState(isLoading = true)
    )

    fun onIntent(intent: NotesIntent) {
        when (intent) {
            is NotesIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

}