package com.ivzb.urgat.ui.wellbeing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.ui.common.UiEventHolder
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellbeingViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<WellbeingUiEvent>()

    private val _searchQuery = MutableStateFlow("")


    val uiState: StateFlow<WellbeingUiState> = combineUiState(
        observeWeightsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = null, limitFlow = null),
        uiEventHolder(),

        onLoading = {
            WellbeingUiState(isLoading = true)
        },
        onError = {
            WellbeingUiState(hasError = true)
        },
        onSuccess = { weights, uiEvent ->
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
            uiEventHolder.emit(
                when (id) {
                    WellbeingFabActionId.Weight -> WellbeingUiEvent.TrackWeight
                }
            )
        }
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

}
