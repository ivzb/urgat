package com.ivzb.urgat.ui.weights

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.ObserveWeightsSortingUseCase
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeightsViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
    observeWeightsSortingUseCase: ObserveWeightsSortingUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedSortType: Flow<WeightsSortType> = savedStateHandle.getStateFlow(WEIGHTS_SORT_SAVED_STATE_KEY, WeightsSortType.BY_DATE)

    val uiState: StateFlow<WeightsUiState> = combineUiState(
        observeWeightsUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = _savedSortType, limitFlow = null),
        observeWeightsSortingUseCase(),

        onLoading = {
            WeightsUiState(isLoading = true)
        },
        onError = {
            WeightsUiState(hasError = true)
        },
        onSuccess = { weights, sorting ->
            WeightsUiState(
                items = weights,
                isLoading = false,
                filteringUiInfo = WeightsFilteringUiInfo(
                    currentFilteringLabel = R.string.all_weights,
                    emptyLabel = R.string.no_weights,
                    sortMenuItems = sorting,
                )
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WeightsUiState(isLoading = true)
    )

    fun onIntent(intent: WeightsIntent) {
        when (intent) {
            is WeightsIntent.SetSorting -> setSorting(intent.sortType)
        }
    }

    private fun setSorting(sortType: WeightsSortType) {
        savedStateHandle[WEIGHTS_SORT_SAVED_STATE_KEY] = sortType
    }
}

const val WEIGHTS_SORT_SAVED_STATE_KEY = "WEIGHTS_SORT_SAVED_STATE_KEY"
