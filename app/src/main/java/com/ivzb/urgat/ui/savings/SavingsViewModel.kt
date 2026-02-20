package com.ivzb.urgat.ui.savings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.domain.saving.ObserveSavingsUiStateUseCase
import com.ivzb.urgat.util.WhileUiSubscribed
import com.ivzb.urgat.util.combineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SavingsViewModel @Inject constructor(
    observeSavingsUiStateUseCase: ObserveSavingsUiStateUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _savedSortType: Flow<SavingsSortType> = savedStateHandle.getStateFlow(SAVINGS_SORT_SAVED_STATE_KEY, SavingsSortType.BY_DATE)

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<SavingsUiState> = combineUiState(
        observeSavingsUiStateUseCase(_searchQuery, _savedSortType),

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
        }
    }

    private fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun setSorting(sortType: SavingsSortType) {
        savedStateHandle[SAVINGS_SORT_SAVED_STATE_KEY] = sortType
    }
}

const val SAVINGS_SORT_SAVED_STATE_KEY = "SAVINGS_SORT_SAVED_STATE_KEY"
