package com.ivzb.urgat.ui.trades

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object TradesRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.trades

}

