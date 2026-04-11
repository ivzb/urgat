package com.ivzb.urgat.ui.inAppUpdate

sealed class InAppUpdateIntent {
    data object CheckForUpdate : InAppUpdateIntent()
    data object CompleteUpdate : InAppUpdateIntent()
    data object UpdateStarted : InAppUpdateIntent()
}

