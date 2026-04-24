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
            is ImportDataIntent.ImportDataFolder -> importDataFolder(intent.uri)

            is ImportDataIntent.SwitchToManualMode -> switchToManualMode()
            is ImportDataIntent.UpdateContent -> updateContent(intent.page, intent.value)
            is ImportDataIntent.ImportDataManually -> importDataManually(intent.page)

            is ImportDataIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun importDataFolder(uri: Uri) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            importDataUseCase(uri)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userMessage = R.string.import_data_done,
                )
            }
        }
    }

    private fun switchToManualMode() {
        _uiState.update {
            it.copy(
                isManualMode = true,
            )
        }
    }

    private fun updateContent(page: ImportDataPage, content: String) {
        _uiState.update {
            it.copy(
                manualDataContent = mapOf(
                    page to content,
                )
            )
        }
    }

    private fun importDataManually(page: ImportDataPage) {
        viewModelScope.launch {
            val content = _uiState.value.manualDataContent[page].orEmpty()
            val mapping = _uiState.value.manualDataMapping[page]

            if (content.isEmpty() || mapping == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userMessage = R.string.import_data_failed,
                    )
                }

                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            try {
                when (page) {
                    ImportDataPage.Expenses -> importDataUseCase.importExpenses(content)
                    ImportDataPage.Investments -> importDataUseCase.importInvestments(content)
                    ImportDataPage.Trades -> importDataUseCase.importTrades(content)
                    ImportDataPage.Savings -> importDataUseCase.importSavings(content)
                    ImportDataPage.Budgets -> importDataUseCase.importBudgets(content)
                    ImportDataPage.Notes -> importDataUseCase.importNotes(content)
                    ImportDataPage.WatchlistAssets -> importDataUseCase.importWatchlistAssets(content)
                    ImportDataPage.Todos -> importDataUseCase.importTodos(content)
                    ImportDataPage.FuelConsumption -> importDataUseCase.importFuelConsumption(content)
                    ImportDataPage.CarMaintenance -> importDataUseCase.importCarMaintenance(content)
                    ImportDataPage.Weights -> importDataUseCase.importWeights(content)
                }

                _uiState.update {
                    it.copy(
                        manualDataContent = mapOf(),
                        isLoading = false,
                        userMessage = R.string.import_data_done,
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userMessage = R.string.import_data_failed,
                    )
                }
            }
        }
   }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(
                userMessage = null,
            )
        }
    }

}
