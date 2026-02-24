package com.ivzb.urgat.ui.investments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    observeInvestmentsUiStateUseCase: ObserveInvestmentsUiStateUseCase,
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    private val _savedFilterType = financeRepository.investmentsFilterTypeFlow
    private val _savedSortType = financeRepository.investmentsSortTypeFlow
    private val _searchQuery = financeRepository.searchQueryFlow

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
        viewModelScope.launch { financeRepository.updateSearchQuery(newQuery) }
    }

    private fun setFiltering(filterType: String) {
        viewModelScope.launch { financeRepository.updateInvestmentsFilterType(filterType) }
    }

    private fun setSorting(sortType: InvestmentsSortType) {
        viewModelScope.launch { financeRepository.updateInvestmentsSortType(sortType) }
    }

}

