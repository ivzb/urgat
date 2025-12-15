package com.ivzb.urgat.ui.wellbeing

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRootRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object WellbeingRoute: UrgatRootRoute() {

    override val titleResId: Int
        get() = R.string.wellbeing

}