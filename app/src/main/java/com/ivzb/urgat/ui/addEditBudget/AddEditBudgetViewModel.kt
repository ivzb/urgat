package com.ivzb.urgat.ui.addEditBudget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.budget.CreateBudgetUseCase
import com.ivzb.urgat.domain.budget.DeleteBudgetUseCase
import com.ivzb.urgat.domain.budget.GetBudgetUseCase
import com.ivzb.urgat.domain.budget.GetLastUsedBudgetCurrencyUseCase
import com.ivzb.urgat.domain.budget.UpdateBudgetUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.sanitize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditBudgetViewModel.Factory::class)
class AddEditBudgetViewModel @AssistedInject constructor(
    @Assisted val route: AddEditBudgetRoute,
    private val getBudgetUseCase: GetBudgetUseCase,
    private val getLastUsedBudgetCurrencyUseCase: GetLastUsedBudgetCurrencyUseCase,
    private val createBudgetUseCase: CreateBudgetUseCase,
    private val updateBudgetUseCase: UpdateBudgetUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private var budgetId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditBudgetUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = budgetId != null,
    ))
    val uiState: StateFlow<AddEditBudgetUiState> = _uiState.asStateFlow()

    init {
        val id = budgetId

        if (id != null) {
            loadBudget(id)
        } else {
            loadLastUsedCurrency()
        }
    }

    fun onIntent(intent: AddEditBudgetIntent) {
        when (intent) {
            is AddEditBudgetIntent.UpdateIncome -> updateIncome(intent.value)
            is AddEditBudgetIntent.UpdateBankStart -> updateBankStart(intent.value)
            is AddEditBudgetIntent.UpdateBankEnd -> updateBankEnd(intent.value)
            is AddEditBudgetIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditBudgetIntent.UpdateDate -> updateDate(intent.value)
            AddEditBudgetIntent.SaveBudget -> saveBudget()
            AddEditBudgetIntent.DeleteBudget -> deleteBudget()
            AddEditBudgetIntent.DuplicateBudget -> duplicateBudget()
            AddEditBudgetIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateIncome(income: String) {
        _uiState.update {
            it.copy(income = income)
        }
    }

    private fun updateBankStart(bankStart: String) {
        _uiState.update {
            it.copy(bankStart = bankStart)
        }
    }

    private fun updateBankEnd(bankEnd: String) {
        _uiState.update {
            it.copy(bankEnd = bankEnd)
        }
    }

    private fun updateCurrency(currency: String) {
        _uiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updateDate(date: Long) {
        _uiState.update {
            it.copy(date = date)
        }
    }

    private fun saveBudget() {
        val fields = listOf(
            uiState.value.income,
            uiState.value.bankStart,
            uiState.value.currency,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_budget_message
                )
            }

            return
        }

        if (budgetId == null) {
            createNewBudget()
        } else {
            updateBudget()
        }
    }

    private fun createNewBudget() = viewModelScope.launch {
        createBudgetUseCase(
            income = uiState.value.income.sanitize().toBigDecimal(),
            bankStart = uiState.value.bankStart.sanitize().toBigDecimal(),
            bankEnd = uiState.value.bankEnd.sanitize().toBigDecimalOrNull(),
            currency = uiState.value.currency.sanitize(),
            date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun deleteBudget() {
        val id = budgetId

        if (id == null) {
            Assert.fail("deleteBudget() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteBudgetUseCase(id)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun duplicateBudget() {
        if (budgetId == null) {
            Assert.fail("duplicateBudget() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        budgetId = null

        _uiState.update {
            it.copy(
                screenTitleResId = R.string.add_budget,
                date = System.currentTimeMillis(),
                withDelete = false,
                withDuplicate = false,
            )
        }
    }

    private fun updateBudget() {
        val id = budgetId

        if (id == null) {
            Assert.fail("updateBudget() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateBudgetUseCase(
                id,
                income = uiState.value.income.sanitize().toBigDecimal(),
                bankStart = uiState.value.bankStart.sanitize().toBigDecimal(),
                bankEnd = uiState.value.bankEnd.sanitize().toBigDecimalOrNull(),
                currency = uiState.value.currency.sanitize(),
                date = uiState.value.date,
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun loadLastUsedCurrency() {
        viewModelScope.launch {
            val currency = getLastUsedBudgetCurrencyUseCase()

            if (currency != null) {
                _uiState.update {
                    it.copy(currency = currency)
                }
            }
        }
    }

    private fun loadBudget(budgetId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getBudgetUseCase(budgetId).let { budget ->
                if (budget != null) {
                    _uiState.update {
                        it.copy(
                            income = budget.income.toString(),
                            bankStart = budget.bankStart.toString(),
                            bankEnd = budget.bankEnd?.toString().orEmpty(),
                            currency = budget.currency,
                            date = budget.date,
                            isLoading = false,
                            withDelete = true,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: AddEditBudgetRoute): AddEditBudgetViewModel
    }

}