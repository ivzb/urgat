package com.ivzb.urgat.ui.addEditWeight

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class AddEditWeightRoute(
    val id: String?,
    @StringRes
    override val titleResId: Int,
): UrgatRoute()