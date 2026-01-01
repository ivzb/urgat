package com.ivzb.urgat.ui.weights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.ObserveWeightsUseCase
import com.ivzb.urgat.ui.todos.TodosIntent
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeightsViewModel @Inject constructor(
    observeWeightsUseCase: ObserveWeightsUseCase,
) : ViewModel() {

    val uiState: StateFlow<WeightsUiState> = combineUiState(
        observeWeightsUseCase(searchQueryFlow = null, filterTypeFlow = null, limitFlow = null),

        onLoading = {
            WeightsUiState(isLoading = true)
        },
        onError = {
            WeightsUiState(hasError = true)
        },
        onSuccess = { weights ->
            WeightsUiState(
                items = weights,
                isLoading = false,
                filteringUiInfo = getFilterUiInfo(),
            )
        }
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WeightsUiState(isLoading = true)
    )

    fun onIntent(intent: WeightsIntent) {
        // NOOP
    }

    private fun getFilterUiInfo(): WeightsFilteringUiInfo {
        return WeightsFilteringUiInfo(
            currentFilteringLabel = R.string.all_weights,
            emptyLabel = R.string.no_weights,
        )
    }
}