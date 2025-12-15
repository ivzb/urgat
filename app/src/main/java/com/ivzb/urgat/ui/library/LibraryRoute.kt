package com.ivzb.urgat.ui.library

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatMenuRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
object LibraryRoute: UrgatMenuRoute() {

    override val titleResId: Int
        get() = R.string.library

}