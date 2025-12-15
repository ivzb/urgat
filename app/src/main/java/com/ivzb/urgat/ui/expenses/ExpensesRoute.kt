package com.ivzb.urgat.ui.expenses

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object ExpensesRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.expenses

    override val withFAB = true

}