package com.ivzb.urgat.ui.addEditWeight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.CreateWeightUseCase
import com.ivzb.urgat.domain.weight.DeleteWeightUseCase
import com.ivzb.urgat.domain.weight.GetWeightUseCase
import com.ivzb.urgat.domain.weight.UpdateWeightUseCase
import com.ivzb.urgat.util.Assert
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditWeightViewModel.Factory::class)
class AddEditWeightViewModel @AssistedInject constructor(
    @Assisted val route: AddEditWeightRoute,
    private val getWeightUseCase: GetWeightUseCase,
    private val createWeightUseCase: CreateWeightUseCase,
    private val updateWeightUseCase: UpdateWeightUseCase,
    private val deleteWeightUseCase: DeleteWeightUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private val weightId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditWeightUiState(
        screenTitleResId = screenTitleResId,
    ))
    val uiState: StateFlow<AddEditWeightUiState> = _uiState.asStateFlow()

    init {
        if (weightId != null) {
            loadWeight(weightId)
        }
    }

    fun onIntent(intent: AddEditWeightIntent) {
        when (intent) {
            is AddEditWeightIntent.UpdateValue -> updateValue(intent.value)
            is AddEditWeightIntent.UpdateDate -> updateDate(intent.date)
            AddEditWeightIntent.SaveWeight -> saveWeight()
            AddEditWeightIntent.DeleteWeight -> deleteWeight()
            AddEditWeightIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateValue(value: String) {
        _uiState.update {
            it.copy(value = value)
        }
    }

    private fun updateDate(date: Long) {
        _uiState.update {
            it.copy(date = date)
        }
    }

    private fun saveWeight() {
        val fields = listOf(
            uiState.value.value,
        )

        val emptyIndex = fields.indexOfFirst { it.isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_weight_message
                )
            }

            return
        }

        if (weightId == null) {
            createNewWeight()
        } else {
            updateWeight()
        }
    }

    private fun createNewWeight() = viewModelScope.launch {
        createWeightUseCase(
           value = uiState.value.value.toBigDecimal(),
           date = uiState.value.date,
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateWeight() {
        if (weightId == null) {
            Assert.fail("updateWeight() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateWeightUseCase(
                weightId,
                value = uiState.value.value.toBigDecimal(),
                date = uiState.value.date,
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteWeight() {
        if (weightId == null) {
            Assert.fail("deleteWeight() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteWeightUseCase(weightId)

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

    private fun loadWeight(weightId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getWeightUseCase(weightId).let { weight ->
                if (weight != null) {
                    _uiState.update {
                        it.copy(
                            value = weight.value.toString(),
                            date = weight.date,
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
        fun create(route: AddEditWeightRoute): AddEditWeightViewModel
    }

}