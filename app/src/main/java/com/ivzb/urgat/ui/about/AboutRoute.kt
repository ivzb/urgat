package com.ivzb.urgat.ui.about

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object AboutRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.about

}