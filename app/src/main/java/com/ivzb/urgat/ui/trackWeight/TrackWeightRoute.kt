package com.ivzb.urgat.ui.trackWeight

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object TrackWeightRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.track_weight

}