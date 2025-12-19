package com.ivzb.urgat.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.note.ObserveNotesUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    observeNotesUseCase: ObserveNotesUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<NotesUiState> = combineUiState(
        observeNotesUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = null, limitFlow = null),

        onLoading = {
            NotesUiState(isLoading = true)
        },
        onError = {
            NotesUiState(hasError = true)
        },
        onSuccess = { notes ->
            NotesUiState(
                items = notes,
            )
        }
    )
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