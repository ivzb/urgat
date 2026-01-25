package com.ivzb.urgat.ui.savings

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object SavingsRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.savings

}