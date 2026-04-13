package com.ivzb.urgat.ui.addEditFuelConsumption

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AddEditFuelConsumptionRoute(
    val id: String?,
    @StringRes
    override val titleResId: Int,
): UrgatRoute()

