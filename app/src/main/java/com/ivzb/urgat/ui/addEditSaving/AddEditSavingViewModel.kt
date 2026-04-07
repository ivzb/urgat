package com.ivzb.urgat.ui.addEditSaving

import kotlin.text.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.saving.CreateSavingUseCase
import com.ivzb.urgat.domain.saving.DeleteSavingUseCase
import com.ivzb.urgat.domain.saving.GetSavingUseCase
import com.ivzb.urgat.domain.saving.UpdateSavingUseCase
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

@HiltViewModel(assistedFactory = AddEditSavingViewModel.Factory::class)
class AddEditSavingViewModel @AssistedInject constructor(
    @Assisted val route: AddEditSavingRoute,
    private val getSavingUseCase: GetSavingUseCase,
    private val createSavingUseCase: CreateSavingUseCase,
    private val updateSavingUseCase: UpdateSavingUseCase,
    private val deleteSavingUseCase: DeleteSavingUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private val savingId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditSavingUiState(
        screenTitleResId = screenTitleResId,
        withDuplicate = savingId != null,
    ))
    val uiState: StateFlow<AddEditSavingUiState> = _uiState.asStateFlow()

    init {
        if (savingId != null) {
            loadSaving(savingId)
        }
    }

    fun onIntent(intent: AddEditSavingIntent) {
        when (intent) {
            is AddEditSavingIntent.UpdateName -> updateName(intent.value)
            is AddEditSavingIntent.UpdateAmount -> updateAmount(intent.value)
            is AddEditSavingIntent.UpdateCurrency -> updateCurrency(intent.value)
            is AddEditSavingIntent.UpdateDate -> updateDate(intent.value)
            AddEditSavingIntent.SaveSaving -> saveSaving()
            AddEditSavingIntent.DeleteSaving -> deleteSaving()
            AddEditSavingIntent.DuplicateSaving -> duplicateSaving()
            AddEditSavingIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    private fun updateAmount(amount: String) {
        _uiState.update {
            it.copy(amount = amount)
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

    private fun saveSaving() {
        val fields = listOf(
            uiState.value.name,
            uiState.value.amount,
            uiState.value.currency,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_saving_message
                )
            }

            return
        }

        if (savingId == null) {
            createNewSaving()
        } else {
            updateSaving()
        }
    }

    private fun createNewSaving() = viewModelScope.launch {
        createSavingUseCase(
            name = uiState.value.name.sanitize(),
            amount = uiState.value.amount.sanitize().toBigDecimal(),
            currency = uiState.value.currency.sanitize(),
            date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateSaving() {
        if (savingId == null) {
            Assert.fail("updateSaving() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateSavingUseCase(
                savingId,
                name = uiState.value.name.sanitize(),
                amount = uiState.value.amount.sanitize().toBigDecimal(),
                currency = uiState.value.currency.sanitize(),
                date = uiState.value.date,
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteSaving() {
        if (savingId == null) {
            Assert.fail("deleteSaving() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteSavingUseCase(savingId)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun duplicateSaving() {
        if (savingId == null) {
            Assert.fail("duplicateSaving() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(date = System.currentTimeMillis())
            }

            createNewSaving()
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun loadSaving(savingId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getSavingUseCase(savingId).let { saving ->
                if (saving != null) {
                    _uiState.update {
                        it.copy(
                            name = saving.name,
                            amount = saving.amount.toString(),
                            currency = saving.currency,
                            date = saving.date,
                            isLoading = false,
                            withDelete = true
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
        fun create(route: AddEditSavingRoute): AddEditSavingViewModel
    }

}
