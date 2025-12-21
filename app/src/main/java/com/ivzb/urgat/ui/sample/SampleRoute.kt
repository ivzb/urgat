package com.ivzb.urgat.ui.sample

import androidx.compose.runtime.Immutable
import com.ivzb.urgat.R
import com.ivzb.urgat.navigation.UrgatRoute
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data object SampleRoute: UrgatRoute() {

    override val titleResId: Int
        get() = R.string.sample_data

}