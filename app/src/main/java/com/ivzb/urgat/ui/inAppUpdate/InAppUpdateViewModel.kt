package com.ivzb.urgat.ui.inAppUpdate

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.data.inAppUpdate.InAppInstallState
import com.ivzb.urgat.data.inAppUpdate.InAppUpdateStatus
import com.ivzb.urgat.domain.inAppUpdate.CheckForUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.CompleteUpdateUseCase
import com.ivzb.urgat.domain.inAppUpdate.ObserveInstallStateUseCase
import com.ivzb.urgat.domain.inAppUpdate.StartFlexibleUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InAppUpdateViewModel @Inject constructor(
    private val checkForUpdateUseCase: CheckForUpdateUseCase,
    private val observeInstallStateUseCase: ObserveInstallStateUseCase,
    private val startFlexibleUpdateUseCase: StartFlexibleUpdateUseCase,
    private val completeUpdateUseCase: CompleteUpdateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InAppUpdateUiState())
    val uiState: StateFlow<InAppUpdateUiState> = _uiState.asStateFlow()

    init {
        observeInstallState()
        checkForUpdate()
    }

    fun onIntent(intent: InAppUpdateIntent) {
        when (intent) {
            InAppUpdateIntent.CheckForUpdate -> checkForUpdate()
            InAppUpdateIntent.StartUpdate -> { /* handled by startUpdate(activity) */ }
            InAppUpdateIntent.CompleteUpdate -> completeUpdate()
            InAppUpdateIntent.DismissUpdate -> dismissUpdate()
        }
    }

    fun startUpdate(activity: Activity) {
        startFlexibleUpdateUseCase(activity)
    }

    private fun checkForUpdate() {
        viewModelScope.launch {
            when (checkForUpdateUseCase()) {
                InAppUpdateStatus.UpdateAvailable -> {
                    _uiState.update { it.copy(isUpdateAvailable = true, isDismissed = false) }
                }

                InAppUpdateStatus.Downloaded -> {
                    _uiState.update { it.copy(isDownloaded = true, isDownloading = false, isDismissed = false) }
                }

                InAppUpdateStatus.InProgress -> {
                    _uiState.update { it.copy(isDownloading = true, isDismissed = false) }
                }

                InAppUpdateStatus.NoUpdate -> {
                    _uiState.update { it.copy(isUpdateAvailable = false) }
                }
            }
        }
    }

    private fun observeInstallState() {
        viewModelScope.launch {
            observeInstallStateUseCase().collect { installState ->
                when (installState) {
                    is InAppInstallState.Downloading -> {
                        val progress = if (installState.totalBytesToDownload > 0) {
                            installState.bytesDownloaded.toFloat() / installState.totalBytesToDownload.toFloat()
                        } else {
                            0f
                        }

                        _uiState.update {
                            it.copy(
                                isDownloading = true,
                                isUpdateAvailable = false,
                                downloadProgress = progress,
                            )
                        }
                    }

                    InAppInstallState.Downloaded -> {
                        _uiState.update {
                            it.copy(
                                isDownloaded = true,
                                isDownloading = false,
                                isUpdateAvailable = false,
                                isDismissed = false,
                            )
                        }
                    }

                    InAppInstallState.Installed -> {
                        _uiState.update { InAppUpdateUiState() }
                    }

                    InAppInstallState.Failed, InAppInstallState.Canceled -> {
                        _uiState.update {
                            it.copy(
                                isDownloading = false,
                                isUpdateAvailable = true,
                            )
                        }
                    }

                    else -> {
                        // Idle, Installing - no UI change needed
                    }
                }
            }
        }
    }

    private fun completeUpdate() {
        completeUpdateUseCase()
    }

    private fun dismissUpdate() {
        _uiState.update { it.copy(isDismissed = true) }
    }
}