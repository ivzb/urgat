package com.ivzb.urgat.ui.investments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.base.BaseRepository.Companion.NO_LIMIT
import com.ivzb.urgat.data.investment.Investment
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
    observeInvestmentsUseCase: ObserveInvestmentsUseCase,
    observeInvestmentsFilteringUseCase: ObserveInvestmentsFilteringUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedFilterType = savedStateHandle.getStateFlow(INVESTMENTS_FILTER_SAVED_STATE_KEY, initialValue = null)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<InvestmentsUiState> = combine(
        observeInvestmentsUseCase(_searchQuery, _savedFilterType, NO_LIMIT),
        observeInvestmentsFilteringUseCase(),
    ) { investmentsAsync, investmentsFilteringAsync ->

        val items = listOf(investmentsAsync, investmentsFilteringAsync)

        when {
            items.any { it is Async.Loading } -> {
                InvestmentsUiState(
                    isLoading = true
                )
            }

            items.any { it is Async.Error } -> {
                InvestmentsUiState(
                    hasError = true
                )
            }

            items.all { it is Async.Success } -> {
                InvestmentsUiState(
                    items = (investmentsAsync as Async.Success<List<Investment>>).data,
                    isLoading = false,
                    filteringUiInfo = InvestmentsFilteringUiInfo(
                        filterMenuItems = (investmentsFilteringAsync as Async.Success<List<InvestmentsFilterMenuItem>>).data
                    )
                )
            }

            else -> error("undefined state: $items")
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
            is InvestmentsIntent.SetFiltering -> setFiltering(intent.filter)
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setFiltering(filter: InvestmentsFilterMenuItem) {
        savedStateHandle[INVESTMENTS_FILTER_SAVED_STATE_KEY] = filter
    }

}

const val INVESTMENTS_FILTER_SAVED_STATE_KEY = "INVESTMENTS_FILTER_SAVED_STATE_KEY"
