package com.ivzb.urgat.ui.todos

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object TodosRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.todo

    override val withFAB = true

}