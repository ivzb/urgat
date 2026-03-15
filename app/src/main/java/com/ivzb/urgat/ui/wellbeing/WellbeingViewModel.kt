package com.ivzb.urgat.ui.wellbeing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.data.resources.ResourcesProvider
import com.ivzb.urgat.ui.components.UiEventHolder
import com.ivzb.urgat.domain.weight.ObserveWeightsSortingUseCase
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.ui.weights.WeightsFilteringUiInfo
import com.ivzb.urgat.ui.weights.WeightsSortType
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WellbeingViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
    observeWeightsSortingUseCase: ObserveWeightsSortingUseCase,
    resourcesProvider: ResourcesProvider,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val uiEventHolder = UiEventHolder<WellbeingUiEvent>()

    private val _itemsLimit = MutableStateFlow(resourcesProvider.getInteger(R.integer.items_limit))

    private val _savedWeightsSortType: Flow<WeightsSortType> = savedStateHandle.getStateFlow(WELLBEING_WEIGHTS_SORT_SAVED_STATE_KEY, WeightsSortType.BY_DATE)

    val uiState: StateFlow<WellbeingUiState> = combineUiState(
        observeWeightsUseCase(searchQueryFlow = null, dateRangeFlow = null, filterTypeFlow = null, sortTypeFlow = _savedWeightsSortType, limitFlow = _itemsLimit),
        observeWeightsSortingUseCase(),
        _savedWeightsSortType.map { Async.Success(it) },
        uiEventHolder(),

        onLoading = {
            WellbeingUiState(isLoading = true)
        },
        onError = {
            WellbeingUiState(hasError = true)
        },
        onSuccess = { weights, sorting, sortType, uiEvent ->
            WellbeingUiState(
                weights = weights,
                weightsFilteringUiInfo = WeightsFilteringUiInfo(
                    currentFilteringLabel = R.string.recent_weights,
                    sortMenuItems = sorting,
                    showSortBadge = sortType != WeightsSortType.BY_DATE,
                ),
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
            is WellbeingIntent.FabActionClick -> onFabActionClick(intent.itemId)
            is WellbeingIntent.SetWeightsSorting -> setWeightsSorting(intent.sortType)
            is WellbeingIntent.EventConsumed -> onEventConsumed()
        }
    }

    private fun onFabActionClick(id: WellbeingFabActionId) {
        viewModelScope.launch {
            uiEventHolder.emit(
                when (id) {
                    WellbeingFabActionId.Weight -> WellbeingUiEvent.AddWeight
                }
            )
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

