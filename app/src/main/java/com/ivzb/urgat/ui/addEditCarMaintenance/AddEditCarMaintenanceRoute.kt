package com.ivzb.urgat.ui.addEditCarMaintenance

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AddEditCarMaintenanceRoute(
    val id: String?,
    @StringRes
    override val titleResId: Int,
): UrgatRoute()

