package com.ivzb.urgat.ui.exportData

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.exportData.ExportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportDataViewModel @Inject constructor(
    private val exportDataUseCase: ExportDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportDataUiState())
    val uiState: StateFlow<ExportDataUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ExportDataIntent) {
        when (intent) {
            is ExportDataIntent.ExportDataData -> exportData(intent.uri)
            is ExportDataIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun exportData(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            exportDataUseCase(uri)

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
                userMessage = null,
                shouldNavigateBack = true,
            )
        }
    }

}
