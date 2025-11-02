package com.ivzb.urgat.ui.trackers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.ui.components.HeaderItem
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackersViewModel @Inject constructor() : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    private val _uiEvent = MutableStateFlow<TrackersUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TrackersUiState> = combine(
        _isLoading, _uiEvent
    ) { isLoading, uiEvent ->

        TrackersUiState(
            headerItems = listOf(
                HeaderItem(
                    TrackersHeaderItemId.Weight,
                    Icons.Filled.Timeline,
                    R.string.track_weight
                ),
            ),
            isLoading = false,
            uiEvent = uiEvent,
        )
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

    private fun onHeaderItemClick(item: TrackerHeaderItem) {
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
