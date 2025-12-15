package com.ivzb.urgat.ui.finance

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatMenuRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object FinanceRoute: UrgatMenuRoute() {

    override val titleResId: Int
        get() = R.string.finance

}