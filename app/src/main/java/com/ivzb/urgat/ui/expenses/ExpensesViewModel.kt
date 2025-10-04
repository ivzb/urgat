package com.ivzb.urgat.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.ivzb.urgat.data.expense.ExpensesRepository
import com.ivzb.urgat.ui.expenses.ExpensesUiState.Error
import com.ivzb.urgat.ui.expenses.ExpensesUiState.Loading
import com.ivzb.urgat.ui.expenses.ExpensesUiState.Success
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository
) : ViewModel() {

    val uiState: StateFlow<ExpensesUiState> = expensesRepository
        .expenses.map<List<String>, ExpensesUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addExpenses(name: String) {
        viewModelScope.launch {
            expensesRepository.add(name)
        }
    }
}

sealed interface ExpensesUiState {
    object Loading : ExpensesUiState
    data class Error(val throwable: Throwable) : ExpensesUiState
    data class Success(val data: List<String>) : ExpensesUiState
}
