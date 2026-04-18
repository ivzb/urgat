package com.ivzb.urgat.ui.addEditCarMaintenance

import androidx.annotation.StringRes

data class AddEditCarMaintenanceUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val description: String = "",
    val partNumber: String = "",
    val url: String = "",
    val carName: String = "",
    val odometer: String = "",
    val date: Long = System.currentTimeMillis(),
    val focusIndex: Int = 0,
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val withDelete: Boolean = false,
    val withDuplicate: Boolean = false,
    val withClearDefaults: Boolean = false,
    val shouldNavigateBack: Boolean = false
)

