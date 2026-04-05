package com.ivzb.urgat.ui.wellbeing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.domain.weight.ObserveWeightsUiStateUseCase
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellbeingViewModel @Inject constructor(
    observeWeightsUiStateUseCase: ObserveWeightsUiStateUseCase,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<WellbeingUiEvent>()

    private val _savedWeightsSortType: Flow<WeightsSortType> = savedStateHandle.getStateFlow(WELLBEING_WEIGHTS_SORT_SAVED_STATE_KEY, WeightsSortType.BY_DATE)

    val uiState: StateFlow<WellbeingUiState> = combineUiState(
        observeWeightsUiStateUseCase(_savedWeightsSortType),
        uiEventHolder(),

        onLoading = {
            WellbeingUiState(isLoading = true)
        },
        onError = {
            WellbeingUiState(hasError = true)
        },
        onSuccess = { weightsUiState, uiEvent ->
            WellbeingUiState(
                weightsUiState = weightsUiState,
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
            is WellbeingIntent.FabActionClick -> onFabActionClick()
            is WellbeingIntent.SetWeightsSorting -> setWeightsSorting(intent.sortType)
            is WellbeingIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onFabActionClick() {
        viewModelScope.launch {
            uiEventHolder.emit(WellbeingUiEvent.AddWeight)
        }
    }

    private fun setWeightsSorting(sortType: WeightsSortType) {
        savedStateHandle[WELLBEING_WEIGHTS_SORT_SAVED_STATE_KEY] = sortType
    }

    private fun onEventConsumed() {
        viewModelScope.launch {
            uiEventHolder.emit(null)
        }
    }

}

const val WELLBEING_WEIGHTS_SORT_SAVED_STATE_KEY = "WELLBEING_WEIGHTS_SORT_SAVED_STATE_KEY"

