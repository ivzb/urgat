package com.ivzb.urgat.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.note.Note
import com.ivzb.urgat.data.note.NotesRepository
import com.ivzb.urgat.ui.components.FilteringUiInfo
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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
    private val notesRepository: NotesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _searchQuery = MutableStateFlow("")

    private val _filteredNotesAsync =
        _searchQuery
            .debounce(DEBOUNCE_TIME_MILLIS)
            .flatMapLatest { searchQuery ->
                if (searchQuery == "") {
                    notesRepository.getStream()
                } else {
                    notesRepository.getStream(searchQuery = searchQuery)
                }
            }
            .map { Async.Success(data = it) }
            .catch<Async<List<Note>>> { emit(Async.Error(R.string.error_notes)) }

    val uiState: StateFlow<NotesUiState> = combine(
        _isLoading, _filteredNotesAsync
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

    companion object {

        private const val DEBOUNCE_TIME_MILLIS = 300L

    }
    
}

const val NOTES_FILTER_SAVED_STATE_KEY = "NOTES_FILTER_SAVED_STATE_KEY"

data class NotesFilteringUiInfo(
    val currentFilteringLabel: Int = R.string.all_notes,

    override val emptyLabel: Int = R.string.no_notes,
    override val emptyIconRes: Int = R.drawable.logo_no_fill,

    override val loadingLabel: Int = R.string.loading_notes,
    override val loadingIconRes: Int = R.drawable.ic_loading,

    override val errorLabel: Int = R.string.error_notes,
    override val errorIconRes: Int = R.drawable.ic_error,
) : FilteringUiInfo
