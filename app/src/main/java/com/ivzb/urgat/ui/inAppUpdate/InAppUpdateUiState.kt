package com.ivzb.urgat.ui.inAppUpdate

data class InAppUpdateUiState(
    val isUpdateAvailable: Boolean = false,
    val isDownloading: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
    val isDismissed: Boolean = false,
)

