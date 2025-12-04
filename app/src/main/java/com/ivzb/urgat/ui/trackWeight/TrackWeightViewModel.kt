package com.ivzb.urgat.ui.trackWeight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.weight.CreateWeightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackWeightViewModel @Inject constructor(
    private val createWeightUseCase: CreateWeightUseCase,
): ViewModel() {

    private val _uiState = MutableStateFlow(TrackWeightUiState(
        screenTitleResId = R.string.track_weight,
    ))
    val uiState: StateFlow<TrackWeightUiState> = _uiState.asStateFlow()

    fun onIntent(intent: TrackWeightIntent) {
        when (intent) {
            is TrackWeightIntent.UpdateValue -> updateValue(intent.value)
            is TrackWeightIntent.UpdateDate -> updateDate(intent.date)
            TrackWeightIntent.SaveWeight -> saveWeight()
            TrackWeightIntent.UserMessageShown -> userMessageShown()
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

        createNewWeight()
    }

    private fun createNewWeight() = viewModelScope.launch {
        createWeightUseCase(
           value = uiState.value.value.toBigDecimal(),
           date = uiState.value.date,
        )

        _uiState.update {
            it.copy(isWeightSaved = true)
        }
    }


    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

}