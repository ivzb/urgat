package com.ivzb.urgat.ui.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.finance.FinanceRepository
import com.ivzb.urgat.domain.saving.ObserveSavingsUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SavingsViewModel @Inject constructor(
    observeSavingsUiStateUseCase: ObserveSavingsUiStateUseCase,
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    private val savedSortTypeFlow = financeRepository.savingsSortTypeFlow
    private val searchQueryFlow = financeRepository.searchQueryFlow

    val uiState: StateFlow<SavingsUiState> = combineUiState(
        observeSavingsUiStateUseCase(searchQueryFlow, savedSortTypeFlow),

        onLoading = { SavingsUiState(isLoading = true) },
        onError = { SavingsUiState(hasError = true) },
        onSuccess = { savingsUiState -> savingsUiState },
    )
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = SavingsUiState(isLoading = true)
        )

    fun onIntent(intent: SavingsIntent) {
        when (intent) {
            is SavingsIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is SavingsIntent.SetSorting -> setSorting(intent.sortType)
            is SavingsIntent.ResetFilters -> resetFilters()
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        viewModelScope.launch {
            financeRepository.updateSearchQuery(newQuery)
        }
    }

    private fun setSorting(sortType: SavingsSortType) {
        viewModelScope.launch {
            financeRepository.updateSavingsSortType(sortType)
        }
    }

    private fun resetFilters() {
        onSearchQueryChanged("")
        setSorting(SavingsSortType.BY_DATE)
    }
}
