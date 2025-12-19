package com.ivzb.urgat.ui.wellbeing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.ui.library.LibraryUiState
import com.ivzb.urgat.ui.notes.NotesFilteringUiInfo
import com.ivzb.urgat.ui.todos.TodosFilteringUiInfo
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellbeingViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
) : ViewModel() {

    private val _uiEvent = MutableStateFlow<WellbeingUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<WellbeingUiState> = combineUiState(
        observeWeightsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = null, limitFlow = null),
        // dummy Flow<Async> is needed to avoid combineUiState overload clashes
        MutableStateFlow<Async<Unit>>(Async.Success(Unit)),
        _uiEvent,

        onLoading = {
            WellbeingUiState(isLoading = true)
        },
        onError = {
            WellbeingUiState(hasError = true)
        },
        onSuccess = { weights, _, uiEvent ->
            WellbeingUiState(
                weights = weights,
                isLoading = false,
                uiEvent = uiEvent,
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WellbeingUiState(isLoading = true)
    )

    fun onIntent(intent: WellbeingIntent) {
        when (intent) {
            is WellbeingIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is WellbeingIntent.FabActionClick -> onFabActionClick(intent.itemId)
            is WellbeingIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun onFabActionClick(id: WellbeingFabActionId) {
        viewModelScope.launch {
            _uiEvent.emit(
                when (id) {
                    WellbeingFabActionId.Weight -> WellbeingUiEvent.TrackWeight
                }
            )
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            _uiEvent.emit(null)
        }
    }

}
