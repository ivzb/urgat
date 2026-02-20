package com.ivzb.urgat.ui.investments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    observeInvestmentsUiStateUseCase: ObserveInvestmentsUiStateUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(INVESTMENTS_FILTER_SAVED_STATE_KEY, initialValue = InvestmentsFilterType_All)
    private val _savedSortType: Flow<InvestmentsSortType> = savedStateHandle.getStateFlow(INVESTMENTS_SORT_SAVED_STATE_KEY, InvestmentsSortType.BY_DATE)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<InvestmentsUiState> = combineUiState(
        observeInvestmentsUiStateUseCase(_searchQuery, _savedFilterType, _savedSortType),

        onLoading = { InvestmentsUiState(isLoading = true) },
        onError = { InvestmentsUiState(hasError = true) },
        onSuccess = { investmentsUiState -> investmentsUiState },
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = InvestmentsUiState(isLoading = true)
    )

    fun onIntent(intent: InvestmentsIntent) {
        when (intent) {
            is InvestmentsIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is InvestmentsIntent.SetFiltering -> setFiltering(intent.filterType)
            is InvestmentsIntent.SetSorting -> setSorting(intent.sortType)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: String) {
        savedStateHandle[INVESTMENTS_FILTER_SAVED_STATE_KEY] = filterType
    }

    private fun setSorting(sortType: InvestmentsSortType) {
        savedStateHandle[INVESTMENTS_SORT_SAVED_STATE_KEY] = sortType
    }

}

const val INVESTMENTS_FILTER_SAVED_STATE_KEY = "INVESTMENTS_FILTER_SAVED_STATE_KEY"
const val INVESTMENTS_SORT_SAVED_STATE_KEY = "INVESTMENTS_SORT_SAVED_STATE_KEY"
