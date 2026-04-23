package com.ivzb.urgat.ui.addEditTrade

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AddEditTradeRoute(
    val id: String?,
    @StringRes
    override val titleResId: Int,
): UrgatRoute()

