package com.ivzb.urgat.ui.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.sample.SampleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val sampleUseCase: SampleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SampleUiState())
    val uiState: StateFlow<SampleUiState> = _uiState.asStateFlow()

    fun onIntent(intent: SampleIntent) {
        when (intent) {
            is SampleIntent.AddSampleData -> addSampleData()
            is SampleIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun addSampleData() {
        viewModelScope.launch {
            sampleUseCase()

            _uiState.update {
                it.copy(
                    isLoading = true,
                    userMessage = R.string.sample_data_added,
                )
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(
                userMessage = null,
                shouldNavigateBack = true,
            )
        }
    }

}
