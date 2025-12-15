package com.ivzb.urgat.ui.investments

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatMenuRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object InvestmentsRoute: UrgatMenuRoute() {

    override val titleResId: Int
        get() = R.string.investments

    override val withFAB = true

}