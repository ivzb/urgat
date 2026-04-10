package com.ivzb.urgat.data.inAppUpdate

sealed interface InAppInstallState {
    data object Idle : InAppInstallState
    data class Downloading(val bytesDownloaded: Long, val totalBytesToDownload: Long) : InAppInstallState
    data object Downloaded : InAppInstallState
    data object Installing : InAppInstallState
    data object Installed : InAppInstallState
    data object Failed : InAppInstallState
    data object Canceled : InAppInstallState
}

