package com.ivzb.urgat.ui.weights

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRootRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object WeightsRoute: UrgatRootRoute() {

    override val titleResId: Int
        get() = R.string.weights

}