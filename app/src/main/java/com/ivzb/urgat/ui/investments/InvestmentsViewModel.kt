package com.ivzb.urgat.ui.investments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentCategoriesYearlyUseCase
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentAssetsUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentCategoriesUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentNetWorthUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    observeGroupedInvestmentsUseCase: ObserveGroupedInvestmentsUseCase,
    observeGroupedInvestmentCategoriesYearlyUseCase: ObserveGroupedInvestmentCategoriesYearlyUseCase,
    observeInvestmentAssetsUseCase: ObserveInvestmentAssetsUseCase,
    observeInvestmentCategoriesUseCase: ObserveInvestmentCategoriesUseCase,
    observeInvestmentNetWorthUseCase: ObserveInvestmentNetWorthUseCase,
    observeInvestmentsFilteringUseCase: ObserveInvestmentsFilteringUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(INVESTMENTS_FILTER_SAVED_STATE_KEY, initialValue = InvestmentsFilterType_All)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<InvestmentsUiState> = combineUiState(
        observeGroupedInvestmentsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeGroupedInvestmentCategoriesYearlyUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeInvestmentAssetsUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeInvestmentCategoriesUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeInvestmentNetWorthUseCase(searchQueryFlow = _searchQuery, filterTypeFlow = _savedFilterType, limitFlow = null),
        observeInvestmentsFilteringUseCase(),

        onLoading = {
            InvestmentsUiState(isLoading = true)
        },
        onError = {
            InvestmentsUiState(hasError = true)
        },
        onSuccess = { investments, categoriesYearly, assets, categories, netWorth, filtering ->
            InvestmentsUiState(
                investments = investments,
                categoriesYearly = categoriesYearly,
                assets = assets,
                categories = categories,
                netWorth = netWorth,
                filteringUiInfo = InvestmentsFilteringUiInfo(
                    filterMenuItems = filtering,
                ),
            )
        }
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
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filterType: String) {
        savedStateHandle[INVESTMENTS_FILTER_SAVED_STATE_KEY] = filterType
    }

}

const val INVESTMENTS_FILTER_SAVED_STATE_KEY = "INVESTMENTS_FILTER_SAVED_STATE_KEY"
