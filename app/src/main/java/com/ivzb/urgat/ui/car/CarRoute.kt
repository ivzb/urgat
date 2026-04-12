package com.ivzb.urgat.ui.car

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRootRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object CarRoute: UrgatRootRoute() {

    override val titleResId: Int
        get() = R.string.car

}