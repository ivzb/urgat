package com.ivzb.urgat.ui.addEditWatchlistAsset

import kotlin.text.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivzb.urgat.R
import com.ivzb.urgat.domain.watchlist.CreateWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.DeleteWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.GetWatchlistAssetUseCase
import com.ivzb.urgat.domain.watchlist.UpdateWatchlistAssetUseCase
import com.ivzb.urgat.util.Assert
import com.ivzb.urgat.util.sanitize
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditWatchlistAssetViewModel.Factory::class)
class AddEditWatchlistAssetViewModel @AssistedInject constructor(
    @Assisted val route: AddEditWatchlistAssetRoute,
    private val getWatchlistAssetUseCase: GetWatchlistAssetUseCase,
    private val createWatchlistAssetUseCase: CreateWatchlistAssetUseCase,
    private val updateWatchlistAssetUseCase: UpdateWatchlistAssetUseCase,
    private val deleteWatchlistAssetUseCase: DeleteWatchlistAssetUseCase,
): ViewModel() {

    private val screenTitleResId: Int = route.titleResId
    private val watchlistAssetId: String? = route.id

    private val _uiState = MutableStateFlow(AddEditWatchlistAssetUiState(
        screenTitleResId = screenTitleResId,
    ))
    val uiState: StateFlow<AddEditWatchlistAssetUiState> = _uiState.asStateFlow()

    init {
        if (watchlistAssetId != null) {
            loadWatchlistAsset(watchlistAssetId)
        }
    }

    fun onIntent(intent: AddEditWatchlistAssetIntent) {
        when (intent) {
            is AddEditWatchlistAssetIntent.UpdateName -> updateName(intent.value)
            AddEditWatchlistAssetIntent.SaveWatchlistAsset -> saveWatchlistAsset()
            AddEditWatchlistAssetIntent.DeleteWatchlistAsset -> deleteWatchlistAsset()
            AddEditWatchlistAssetIntent.UserMessageShown -> userMessageShown()
        }
    }

    private fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    private fun saveWatchlistAsset() {
        val fields = listOf(
            uiState.value.name,
        )

        val emptyIndex = fields.indexOfFirst { it.sanitize().isEmpty() }

        if (emptyIndex != -1) {
            _uiState.update {
                it.copy(
                    focusIndex = emptyIndex,
                    userMessage = R.string.empty_watchlist_asset_message
                )
            }

            return
        }

        if (watchlistAssetId == null) {
            createNewWatchlistAsset()
        } else {
            updateWatchlistAsset()
        }
    }

    private fun createNewWatchlistAsset() = viewModelScope.launch {
        createWatchlistAssetUseCase(
            name = uiState.value.name.sanitize(),
        )

        _uiState.update {
            it.copy(shouldNavigateBack = true)
        }
    }

    private fun updateWatchlistAsset() {
        if (watchlistAssetId == null) {
            Assert.fail("updateWatchlistAsset() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            updateWatchlistAssetUseCase(
                watchlistAssetId,
                name = uiState.value.name.sanitize(),
            )

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun deleteWatchlistAsset() {
        if (watchlistAssetId == null) {
            Assert.fail("deleteWatchlistAsset() was called but id is null.")

            _uiState.update {
                it.copy(userMessage = R.string.something_went_wrong)
            }

            return
        }

        viewModelScope.launch {
            deleteWatchlistAssetUseCase(watchlistAssetId)

            _uiState.update {
                it.copy(shouldNavigateBack = true)
            }
        }
    }

    private fun userMessageShown() {
        _uiState.update {
            it.copy(userMessage = null)
        }
    }

    private fun loadWatchlistAsset(watchlistAssetId: String) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getWatchlistAssetUseCase(watchlistAssetId).let { watchlistAsset ->
                if (watchlistAsset != null) {
                    _uiState.update {
                        it.copy(
                            name = watchlistAsset.name,
                            isLoading = false,
                            withDelete = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(route: AddEditWatchlistAssetRoute): AddEditWatchlistAssetViewModel
    }

}