package com.ivzb.urgat.ui.investments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_ITEMS_LIMIT
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.domain.investment.ObserveGroupedInvestmentsUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsFilteringUseCase
import com.ivzb.urgat.domain.investment.ObserveInvestmentsUseCase
import com.ivzb.urgat.util.Async
import com.ivzb.urgat.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    observeGroupedInvestmentsUseCase: ObserveGroupedInvestmentsUseCase,
    observeInvestmentsFilteringUseCase: ObserveInvestmentsFilteringUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(INVESTMENTS_FILTER_SAVED_STATE_KEY, initialValue = InvestmentsFilterType_All)

    private val _searchQuery = MutableStateFlow("")

    private val _itemsLimit = MutableStateFlow(NO_ITEMS_LIMIT)

    val uiState: StateFlow<InvestmentsUiState> = combine(
        observeGroupedInvestmentsUseCase(_searchQuery, _savedFilterType, _itemsLimit),
        observeInvestmentsFilteringUseCase(),
    ) { investmentsAsync, investmentsFilteringAsync ->

        when {
            investmentsAsync is Async.Loading || investmentsFilteringAsync is Async.Loading -> {
                InvestmentsUiState(
                    isLoading = true
                )
            }

            investmentsAsync is Async.Error || investmentsFilteringAsync is Async.Error -> {
                InvestmentsUiState(
                    hasError = true
                )
            }

            investmentsAsync is Async.Success && investmentsFilteringAsync is Async.Success -> {
                InvestmentsUiState(
                    items = investmentsAsync.data,
                    isLoading = false,
                    filteringUiInfo = InvestmentsFilteringUiInfo(
                        filterMenuItems = (investmentsFilteringAsync as Async.Success<List<InvestmentsFilterMenuItem>>).data
                    )
                )
            }

            else -> error("undefined state: $investmentsAsync, $investmentsFilteringAsync")
        }
    }
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
