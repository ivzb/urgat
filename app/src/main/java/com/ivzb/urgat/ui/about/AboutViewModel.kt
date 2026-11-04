package com.ivzb.urgat.ui.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState)
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    fun onIntent(intent: AboutIntent) {
        // NOOP
    }

}
