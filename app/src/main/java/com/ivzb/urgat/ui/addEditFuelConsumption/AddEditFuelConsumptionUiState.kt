package com.ivzb.urgat.ui.addEditFuelConsumption

import androidx.annotation.StringRes

data class AddEditFuelConsumptionUiState(
    @StringRes
    val screenTitleResId: Int? = null,
    val amount: String = "",
    val price: String = "",
    val currency: String = "",
    val type: String = "",
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

