package com.ivzb.urgat.ui.inAppUpdate

data class InAppUpdateUiState(
    val shouldStartUpdate: Boolean = false,
    val isDownloading: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadProgress: Float = 0f,
)

