package com.ivzb.urgat.ui.splash

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRootRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
object SplashRoute: UrgatRootRoute() {

    override val titleResId: Int
        get() = R.string.splash

}