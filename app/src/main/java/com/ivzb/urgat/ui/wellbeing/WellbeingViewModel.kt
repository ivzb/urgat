package com.ivzb.urgat.ui.wellbeing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
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
class WellbeingViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
) : ViewModel() {

    private val _uiEvent = MutableStateFlow<WellbeingUiEvent?>(null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<WellbeingUiState> = combine(
        _uiEvent,
        observeWeightsUseCase(_searchQuery, null, NO_LIMIT),
    ) { uiEvent, weightsAsync ->

        when (weightsAsync) {
            Async.Loading -> {
                WellbeingUiState(
                    isLoading = true
                )
            }

            is Async.Error -> {
                WellbeingUiState(
                    hasError = true
                )
            }

            is Async.Success -> {
                WellbeingUiState(
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
        initialValue = WellbeingUiState(isLoading = true)
    )

    fun onIntent(intent: WellbeingIntent) {
        when (intent) {
            is WellbeingIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is WellbeingIntent.HeaderItemClick -> onHeaderItemClick(intent.itemId)
            is WellbeingIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun onHeaderItemClick(id: WellbeingHeaderItemId) {
        viewModelScope.launch {
            _uiEvent.emit(
                when (id) {
                    WellbeingHeaderItemId.Weight -> WellbeingUiEvent.TrackWeight
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
