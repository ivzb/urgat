package com.ivzb.urgat.ui.trackers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackersViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _uiEvent = MutableStateFlow<TrackersUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TrackersUiState> = combine(
        _isLoading,
        _uiEvent,
        observeWeightsUseCase(_searchQuery),
    ) { isLoading, uiEvent, weightsAsync ->

        when (weightsAsync) {
            Async.Loading -> {
                TrackersUiState(
                    isLoading = true
                )
            }

            is Async.Error -> {
                TrackersUiState(
                    hasError = true
                )
            }

            is Async.Success -> {
                TrackersUiState(
                    headerItems = listOf(
                        HeaderItem(
                            TrackersHeaderItemId.Weight,
                            Icons.Filled.FitnessCenter,
                            R.string.track_weight
                        ),
                    ),
                    weights = weightsAsync.data,
                    isLoading = false,
                    uiEvent = uiEvent,
                )
            }
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TrackersUiState(isLoading = true)
    )

    fun onIntent(intent: TrackersIntent) {
        when (intent) {
            is TrackersIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is TrackersIntent.HeaderItemClick -> onHeaderItemClick(intent.item)
            is TrackersIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun onHeaderItemClick(item: TrackersHeaderItem) {
        viewModelScope.launch {
            _uiEvent.emit(
                when (item.id) {
                    TrackersHeaderItemId.Weight -> TrackersUiEvent.TrackWeight
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
