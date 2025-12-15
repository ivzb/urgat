package com.ivzb.urgat.ui.wellbeing

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatMenuRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object WellbeingRoute: UrgatMenuRoute() {

    override val titleResId: Int
        get() = R.string.wellbeing

}