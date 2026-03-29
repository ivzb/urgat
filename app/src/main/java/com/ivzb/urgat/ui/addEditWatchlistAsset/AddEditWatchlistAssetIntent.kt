package com.ivzb.urgat.ui.addEditWatchlistAsset

sealed class AddEditWatchlistAssetIntent {
    data class UpdateName(val value: String): AddEditWatchlistAssetIntent()
    object SaveWatchlistAsset: AddEditWatchlistAssetIntent()
    object DeleteWatchlistAsset: AddEditWatchlistAssetIntent()
    object UserMessageShown: AddEditWatchlistAssetIntent()
}