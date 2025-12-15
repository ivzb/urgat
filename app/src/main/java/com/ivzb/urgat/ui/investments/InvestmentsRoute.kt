package com.ivzb.urgat.ui.investments

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object InvestmentsRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.investments

}