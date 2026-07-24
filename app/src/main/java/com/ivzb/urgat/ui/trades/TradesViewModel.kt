package com.ivzb.urgat.ui.trades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.domain.trades.ObserveTradesUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradesViewModel @Inject constructor(
    observeTradesUiStateUseCase: ObserveTradesUiStateUseCase,
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    private val _savedSortType = financeRepository.tradesSortTypeFlow
    private val _searchQuery = financeRepository.searchQueryFlow

    val uiState: StateFlow<TradesUiState> = combineUiState(
        observeTradesUiStateUseCase(_searchQuery, _savedSortType),

        onLoading = { TradesUiState(isLoading = true) },
        onError = { TradesUiState(hasError = true) },
        onSuccess = { tradesUiState -> tradesUiState },
    )
    .stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = TradesUiState(isLoading = true)
    )

    fun onIntent(intent: TradesIntent) {
        when (intent) {
            is TradesIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is TradesIntent.SetSorting -> setSorting(intent.sortType)
            is TradesIntent.ResetFilters -> resetFilters()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            financeRepository.updateSearchQuery(newQuery)
        }
    }

    private fun setSorting(sortType: TradesSortType) {
        viewModelScope.launch {
            financeRepository.updateTradesSortType(sortType)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setSorting(TradesSortType.BY_DATE)
    }
}

