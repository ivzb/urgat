package com.ivzb.urgat.ui.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.export.ExportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportUseCase: ExportUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ExportIntent) {
        when (intent) {
            is ExportIntent.ExportData -> exportData()
            is ExportIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun exportData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            exportUseCase()

            _uiState.update {
                it.copy(
                    userMessage = R.string.export_data_done,
                )
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(
                isLoading = false,
                userMessage = null,
            )
        }
    }

}
