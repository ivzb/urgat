package com.ivzb.urgat.ui.importData

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.importData.ImportDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportDataViewModel @Inject constructor(
    private val importDataUseCase: ImportDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportDataUiState())
    val uiState: StateFlow<ImportDataUiState> = _uiState.asStateFlow()

    fun onIntent(intent: ImportDataIntent) {
        when (intent) {
            is ImportDataIntent.ImportData -> importData(intent.uri)
            is ImportDataIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun importData(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            importDataUseCase(uri)

            _uiState.update {
                it.copy(
                    userMessage = R.string.import_data_done,
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
