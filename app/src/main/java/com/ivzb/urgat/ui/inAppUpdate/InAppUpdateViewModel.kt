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
import com.ivzb.urgat.util.TimeProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val UPDATE_DEBOUNCE_MS = 30 * 60 * 1000L // 30 minutes

@HiltViewModel
class InAppUpdateViewModel @Inject constructor(
    private val checkForUpdateUseCase: CheckForUpdateUseCase,
    private val observeInstallStateUseCase: ObserveInstallStateUseCase,
    private val startFlexibleUpdateUseCase: StartFlexibleUpdateUseCase,
    private val completeUpdateUseCase: CompleteUpdateUseCase,
    private val timeProvider: TimeProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InAppUpdateUiState())
    val uiState: StateFlow<InAppUpdateUiState> = _uiState.asStateFlow()

    private var lastUpdatePromptTime: Long? = null

    init {
        observeInstallState()
        checkForUpdate()
    }

    fun onIntent(intent: InAppUpdateIntent) {
        when (intent) {
            InAppUpdateIntent.CheckForUpdate -> checkForUpdate()
            InAppUpdateIntent.CompleteUpdate -> completeUpdate()
            InAppUpdateIntent.UpdateStarted -> consumeStartUpdate()
        }
    }

    fun startUpdate(activity: Activity) {
        startFlexibleUpdateUseCase(activity)
    }

    private fun checkForUpdate() {
        val state = _uiState.value
        
        if (state.shouldStartUpdate || state.isDownloading || state.isDownloaded) {
            return
        }

        val lastPrompt = lastUpdatePromptTime
        val now = timeProvider.currentTimeMillis()

        if (lastPrompt != null && now - lastPrompt < UPDATE_DEBOUNCE_MS) {
            return
        }

        viewModelScope.launch {
            when (checkForUpdateUseCase()) {
                InAppUpdateStatus.UpdateAvailable -> {
                    lastUpdatePromptTime = now
                    _uiState.update { it.copy(shouldStartUpdate = true) }
                }

                InAppUpdateStatus.Downloaded -> {
                    _uiState.update { it.copy(isDownloaded = true, isDownloading = false) }
                }

                InAppUpdateStatus.InProgress -> {
                    _uiState.update { it.copy(isDownloading = true) }
                }

                InAppUpdateStatus.NoUpdate -> {
                    _uiState.update { it.copy(shouldStartUpdate = false) }
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
                                shouldStartUpdate = false,
                                downloadProgress = progress,
                            )
                        }
                    }

                    InAppInstallState.Downloaded -> {
                        _uiState.update {
                            it.copy(
                                isDownloaded = true,
                                isDownloading = false,
                                shouldStartUpdate = false,
                            )
                        }
                    }

                    InAppInstallState.Installed -> {
                        _uiState.update { InAppUpdateUiState() }
                    }

                    InAppInstallState.Failed, InAppInstallState.Canceled -> {
                        lastUpdatePromptTime = timeProvider.currentTimeMillis()
                        _uiState.update {
                            it.copy(
                                isDownloading = false,
                                shouldStartUpdate = false,
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

    private fun consumeStartUpdate() {
        _uiState.update { it.copy(shouldStartUpdate = false) }
    }
}