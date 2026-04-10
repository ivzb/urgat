package com.ivzb.urgat.data.inAppUpdate

sealed interface InAppUpdateStatus {
    data object NoUpdate : InAppUpdateStatus
    data object UpdateAvailable : InAppUpdateStatus
    data object InProgress : InAppUpdateStatus
    data object Downloaded : InAppUpdateStatus
}

