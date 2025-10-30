package com.ivzb.urgat.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.useCase.ObserveNotesUseCase
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class NotesUiState(
    val items: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val filteringUiInfo: NotesFilteringUiInfo = NotesFilteringUiInfo()
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    observeNotesUseCase: ObserveNotesUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<NotesUiState> = combine(
        _isLoading,
        observeNotesUseCase(_searchQuery, null)
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

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

}

data class NotesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_notes,

    override val emptyLabel: Int = R.string.no_notes,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_notes,

    override val errorLabel: Int = R.string.error_notes,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
